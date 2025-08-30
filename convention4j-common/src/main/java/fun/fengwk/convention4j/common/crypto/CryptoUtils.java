package fun.fengwk.convention4j.common.crypto;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * 提供对称加密 (AES/GCM/CBC)、消息摘要 (SHA-256)、消息认证码 (HMAC-SHA256) 和非对称加密 (RSA) 的核心功能。
 * 此类遵循零信任原则，默认提供最安全的算法。
 *
 * <h3>核心特性:</h3>
 * <ul>
 *   <li><b>安全默认:</b> 默认使用 AES/GCM, RSA/OAEP, SHA-256, HMAC-SHA256 等当前行业推荐的强大算法。</li>
 *   <li><b>防误用API:</b> 对称加密返回结构化对象，避免了对底层字节数组的危险操作。</li>
 *   <li><b>时序攻击防护:</b> 提供了常量时间的哈希比较方法 {@link #safeEquals(String, String)}。</li>
 *   <li><b>大文件支持:</b> 支持流式计算文件哈希，内存占用极低。</li>
 *   <li><b>线程安全:</b> 所有方法均为线程安全的。</li>
 * </ul>
 *
 * <h3>遗留算法兼容性:</h3>
 * <p>为了兼容老旧系统，本工具类也包含了已被认为不安全的算法（如 MD5, SHA-1, DES, AES/CBC）。
 * 这些方法均已被标记为 {@code @Deprecated} 或附有详细的风险说明。
 * <b>严禁在任何新项目或安全要求高的场景中使用这些遗留算法。</b>
 *
 * <h3>密钥管理警告:</h3>
 * <p>本工具类不涉及密钥的存储。在生产环境中，密钥绝不能硬编码在代码中。
 * 推荐使用专用的密钥管理系统 (KMS)、硬件安全模块 (HSM) 或 Java Keystore 来安全地存储和管理密钥。
 *
 * @author fengwk
 */
public final class CryptoUtils {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CryptoUtils() {}

    /**
     * 自定义密码学操作异常，用于封装底层检查型异常，简化调用者代码。
     */
    public static class CryptoException extends RuntimeException {
        public CryptoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // =================================================================================================================
    // 辅助工具 (Hex & Safe Compare)
    // =================================================================================================================

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    /**
     * 将字节数组高效地转换为十六进制字符串。
     *
     * @param bytes 待转换的字节数组，不能为null。
     * @return 转换后的十六进制字符串。
     */
    public static String bytes2HexStr(final byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes cannot be null");
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 将十六进制字符串高效地转换为字节数组。
     *
     * @param hexStr 待转换的十六进制字符串，不能为null。
     * @return 转换后的字节数组。
     * @throws IllegalArgumentException 如果字符串长度为奇数或包含非十六进制字符。
     */
    public static byte[] hexStr2Bytes(final String hexStr) {
        Objects.requireNonNull(hexStr, "hexStr cannot be null");
        if (hexStr.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length.");
        }

        int len = hexStr.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(hexStr.charAt(i), 16);
            int low = Character.digit(hexStr.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid character in hex string: " + hexStr);
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }

    /**
     * 以常量时间比较两个哈希字符串，以防止时序攻击 (Timing Attack)。
     * 在验证密码哈希、API签名等安全敏感场景下，应始终使用此方法代替 {@code string.equals()}。
     *
     * @param hexHash1 第一个十六进制哈希字符串。可以为null。
     * @param hexHash2 第二个十六进制哈希字符串。可以为null。
     * @return 如果两个哈希串表示的字节序列完全相等，返回true。
     */
    public static boolean safeEquals(final String hexHash1, final String hexHash2) {
        if (hexHash1 == null || hexHash2 == null) {
            return hexHash1 == hexHash2;
        }
        // 注意：MessageDigest.isEqual 要求字节数组，因此需要先解码
        byte[] a = hexStr2Bytes(hexHash1);
        byte[] b = hexStr2Bytes(hexHash2);
        return MessageDigest.isEqual(a, b);
    }


    // =================================================================================================================
    // 消息摘要 (Hashing)
    // =================================================================================================================

    private static final String SHA_256 = "SHA-256";
    private static final int STREAM_BUFFER_SIZE = 4096;

    /**
     * 计算字节数组的 SHA-256 哈希值。
     * @param data 待计算哈希的字节数组，不能为null。
     * @return 64个字符的十六进制SHA-256哈希字符串。
     */
    public static String sha256Hex(final byte[] data) {
        Objects.requireNonNull(data, "data cannot be null");
        return hashHex(data, SHA_256);
    }

    /**
     * 计算字符串的 SHA-256 哈希值（使用UTF-8编码）。
     * @param data 待计算哈希的原始字符串，不能为null。
     * @return 64个字符的十六进制SHA-256哈希字符串。
     */
    public static String sha256Hex(final String data) {
        Objects.requireNonNull(data, "data cannot be null");
        return hashHex(data.getBytes(DEFAULT_CHARSET), SHA_256);
    }

    /**
     * 计算输入流的 SHA-256 哈希值，适用于大文件，内存占用低。
     * <p><b>注意:</b> 此方法执行完毕后<b>不会</b>关闭传入的 {@code InputStream}，调用方需自行负责流的关闭。
     *
     * @param input InputStream，不能为null。方法执行完毕后该流仍保持打开状态。
     * @return 64个字符的十六进制SHA-256哈希字符串。
     * @throws IOException 如果读取流时发生错误。
     * @throws CryptoException 如果SHA-256算法不可用。
     */
    public static String sha256Hex(final InputStream input) throws IOException {
        Objects.requireNonNull(input, "input stream cannot be null");
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return bytes2HexStr(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("SHA-256 algorithm not found", e);
        }
    }

    private static String hashHex(final byte[] data, final String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return bytes2HexStr(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(algorithm + " algorithm not found", e);
        }
    }


    // =================================================================================================================
    // 消息认证码 (MAC)
    // =================================================================================================================

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 计算 HMAC-SHA256 消息认证码。HMAC能够同时验证消息的完整性和真实性。
     *
     * @param data 待计算MAC的数据。
     * @param key  用于计算的密钥。
     * @return HMAC-SHA256的十六进制字符串。
     */
    public static String hmacSha256Hex(final byte[] data, final SecretKey key) {
        Objects.requireNonNull(data, "data cannot be null");
        Objects.requireNonNull(key, "key cannot be null");
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(key);
            return bytes2HexStr(mac.doFinal(data));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CryptoException("HMAC-SHA256 calculation failed", e);
        }
    }

    /**
     * 从Base64字符串恢复一个用于HMAC的密钥。
     * @param secretBase64 Base64编码的密钥。
     * @return {@link SecretKey} 对象。
     */
    public static SecretKey getHmacKeyFromBase64(final String secretBase64) {
        byte[] decodedKey = Base64.getDecoder().decode(secretBase64);
        return new SecretKeySpec(decodedKey, HMAC_SHA256);
    }


    // =================================================================================================================
    // 对称加密 (Symmetric Encryption)
    // =================================================================================================================

    private static final String AES = "AES";
    private static final String AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String AES_CBC_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int GCM_IV_LENGTH_BYTES = 12; // 96 bits - NIST recommended length for GCM
    private static final int AES_IV_LENGTH_BYTES = 16; // AES block size is 128 bits (16 bytes)
    private static final int GCM_TAG_LENGTH_BITS = 128; // Recommended tag length for security

    /**
     * 对称加密结果的不可变容器，包含初始化向量(IV)和密文。
     * 这种设计使得API是类型安全的，避免了手动拼接和拆分字节数组的风险。
     */
    public static final class SymmetricCiphertext {
        private final byte[] iv;
        private final byte[] ciphertext;

        SymmetricCiphertext(byte[] iv, byte[] ciphertext) {
            this.iv = Objects.requireNonNull(iv);
            this.ciphertext = Objects.requireNonNull(ciphertext);
        }

        /** @return IV的防御性拷贝。 */
        public byte[] getIv() { return iv.clone(); }

        /** @return 密文的防御性拷贝。 */
        public byte[] getCiphertext() { return ciphertext.clone(); }

        /**
         * 将整个对象序列化为单个Base64字符串，格式为 [IV + Ciphertext]。
         * @return Base64编码的字符串。
         */
        public String toBase64() {
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        }

        /**
         * 从Base64字符串反序列化，恢复为 SymmetricCiphertext 对象。
         * 此方法是类型不安全的内部实现，请使用 {@link #fromBase64Gcm(String)} 或 {@link #fromBase64Cbc(String)}。
         */
        private static SymmetricCiphertext fromBase64(String base64, int ivLengthBytes) {
            byte[] combined = Base64.getDecoder().decode(base64);
            if (combined.length < ivLengthBytes) {
                throw new IllegalArgumentException("Invalid Base64 input: too short to contain IV of length " + ivLengthBytes);
            }
            byte[] iv = new byte[ivLengthBytes];
            byte[] ciphertext = new byte[combined.length - ivLengthBytes];
            System.arraycopy(combined, 0, iv, 0, ivLengthBytes);
            System.arraycopy(combined, ivLengthBytes, ciphertext, 0, ciphertext.length);
            return new SymmetricCiphertext(iv, ciphertext);
        }

        /**
         * [兼容GCM] 从Base64字符串反序列化，恢复为 SymmetricCiphertext 对象。
         * 假定IV长度为GCM推荐的12字节。
         * @param base64 Base64编码的 [IV + Ciphertext] 字符串。
         * @return {@link SymmetricCiphertext} 对象。
         */
        public static SymmetricCiphertext fromBase64Gcm(String base64) {
            return fromBase64(base64, GCM_IV_LENGTH_BYTES);
        }

        /**
         * [兼容CBC] 从Base64字符串反序列化，恢复为 SymmetricCiphertext 对象。
         * 假定IV长度为AES块大小的16字节。
         * @param base64 Base64编码的 [IV + Ciphertext] 字符串。
         * @return {@link SymmetricCiphertext} 对象。
         */
        public static SymmetricCiphertext fromBase64Cbc(String base64) {
            return fromBase64(base64, AES_IV_LENGTH_BYTES);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SymmetricCiphertext that = (SymmetricCiphertext) o;
            return Arrays.equals(iv, that.iv) && Arrays.equals(ciphertext, that.ciphertext);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(iv);
            result = 31 * result + Arrays.hashCode(ciphertext);
            return result;
        }
    }

    /**
     * 生成一个AES对称密钥。
     * @param keySizeInBits 密钥位数，必须是 128, 192, 或 256。
     * @return {@link SecretKey} 对象。
     */
    public static SecretKey genAesKey(final int keySizeInBits) {
        if (keySizeInBits != 128 && keySizeInBits != 192 && keySizeInBits != 256) {
            throw new IllegalArgumentException("Invalid AES key size. Must be 128, 192, or 256.");
        }
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES);
            keyGen.init(keySizeInBits, SECURE_RANDOM);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            throw new CryptoException("AES algorithm not found", ex);
        }
    }

    /**
     * 从Base64编码的字符串恢复AES密钥。
     * @param secretBase64 Base64编码的密钥。
     * @return {@link SecretKey} 对象。
     */
    public static SecretKey getAesKeyFromBase64(final String secretBase64) {
        byte[] decodedKey = Base64.getDecoder().decode(secretBase64);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
    }

    /**
     * [推荐] 使用 AES/GCM 模式加密数据。GCM提供了认证加密，能同时保证机密性和完整性。
     * @param plaintext 待加密的明文。
     * @param key AES密钥。
     * @return 一个包含IV和密文的 {@link SymmetricCiphertext} 对象。
     * @throws CryptoException 如果加密失败。
     */
    public static SymmetricCiphertext encryptAesGcm(final byte[] plaintext, final SecretKey key) {
        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);
            return new SymmetricCiphertext(iv, ciphertext);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException("AES/GCM encryption failed", ex);
        }
    }

    /**
     * [推荐] 使用 AES/GCM 模式解密数据。
     * @param encryptedData 包含IV和密文的 {@link SymmetricCiphertext} 对象。需使用 {@link SymmetricCiphertext#fromBase64Gcm(String)} 构建。
     * @param key AES密钥。
     * @return 解密后的明文字节数组。
     * @throws CryptoException 如果解密失败，特别是当认证标签不匹配时（意味着数据可能被篡改或密钥错误）。
     */
    public static byte[] decryptAesGcm(final SymmetricCiphertext encryptedData, final SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, encryptedData.getIv());
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            return cipher.doFinal(encryptedData.getCiphertext());
        } catch (AEADBadTagException e) {
            throw new CryptoException("AES/GCM decryption failed: Authentication tag mismatch. " +
                "The data may have been tampered with or the key is incorrect.", e);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException("AES/GCM decryption failed", ex);
        }
    }

    /**
     * [兼容遗留] 使用 AES/CBC 模式加密数据。
     * <p><b>警告:</b> CBC模式本身不提供数据完整性校验。如果可能，请优先使用 {@link #encryptAesGcm(byte[], SecretKey)}。
     * 如果必须使用CBC，请考虑在应用层额外使用HMAC来保护密文不被篡改。
     *
     * @param plaintext 待加密的明文。
     * @param key AES密钥。
     * @return 一个包含IV和密文的 {@link SymmetricCiphertext} 对象。
     * @throws CryptoException 如果加密失败。
     */
    public static SymmetricCiphertext encryptAesCbc(final byte[] plaintext, final SecretKey key) {
        byte[] iv = new byte[AES_IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);
            return new SymmetricCiphertext(iv, ciphertext);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException("AES/CBC encryption failed", ex);
        }
    }

    /**
     * [兼容遗留] 使用 AES/CBC 模式解密数据。
     *
     * @param encryptedData 包含IV和密文的 {@link SymmetricCiphertext} 对象。需使用 {@link SymmetricCiphertext#fromBase64Cbc(String)} 构建。
     * @param key AES密钥。
     * @return 解密后的明文字节数组。
     * @throws CryptoException 如果解密失败。
     */
    public static byte[] decryptAesCbc(final SymmetricCiphertext encryptedData, final SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(encryptedData.getIv());
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return cipher.doFinal(encryptedData.getCiphertext());
        } catch (GeneralSecurityException ex) {
            throw new CryptoException("AES/CBC decryption failed", ex);
        }
    }


    // =================================================================================================================
    // 遗留与不安全的算法 (Legacy & Insecure Algorithms)
    // 警告: 仅用于兼容无法升级的旧系统。严禁用于新开发！
    // =================================================================================================================

    /**
     * [不安全] 计算字符串的 MD5 哈希值。
     * <p><b>警告:</b> MD5 算法在密码学上已被完全攻破，存在严重的碰撞漏洞。
     * 绝不能用于密码存储、数字签名或任何要求安全性的场景。此方法仅用于兼容旧系统生成校验和。
     *
     * @param data 待计算哈希的原始字符串，不能为null。
     * @return 32个字符的十六进制MD5哈希字符串。
     * @deprecated 请使用 {@link #sha256Hex(String)} 作为安全的替代方案。
     */
    @Deprecated
    public static String md5Hex(String data) {
        Objects.requireNonNull(data, "data cannot be null");
        return hashHex(data.getBytes(DEFAULT_CHARSET), "MD5");
    }

    /**
     * [不安全] 计算字符串的 SHA-1 哈希值。
     * <p><b>警告:</b> SHA-1 算法在密码学上已被证明存在严重的碰撞漏洞。
     * 绝不能用于密码存储、数字签名或任何要求抗碰撞性的场景。此方法仅用于兼容旧系统。
     *
     * @param data 待计算哈希的原始字符串，不能为null。
     * @return 40个字符的十六进制SHA-1哈希字符串。
     * @deprecated 请使用 {@link #sha256Hex(String)} 作为安全的替代方案。
     */
    @Deprecated
    public static String sha1Hex(String data) {
        Objects.requireNonNull(data, "data cannot be null");
        return hashHex(data.getBytes(DEFAULT_CHARSET), "SHA-1");
    }

    private static final String DES = "DES";
    private static final String DES_CBC_TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final int DES_IV_LENGTH_BYTES = 8;

    /**
     * [不安全] 生成一个DES对称密钥。
     * <p><b>警告:</b> DES 是一个过时且不安全的加密标准，其 56 位的密钥长度极易受到暴力破解攻击。
     * 此方法仅为兼容老旧系统提供，严禁用于任何新的应用程序。
     *
     * @return DES {@link SecretKey} 对象。
     * @deprecated 请使用 {@link #genAesKey(int)} 作为安全的替代方案。
     */
    @Deprecated
    public static SecretKey genDesKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(DES);
            keyGen.init(56, SECURE_RANDOM);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            throw new CryptoException("DES algorithm not found", ex);
        }
    }

    /**
     * [不安全] 从Base64字符串恢复DES密钥。仅用于兼容遗留系统。
     * @param secretBase64 base64编码的DES密钥。
     * @return DES {@link SecretKey} 对象。
     * @deprecated 请使用 {@link #getAesKeyFromBase64(String)}。
     */
    @Deprecated
    public static SecretKey getDesKeyFromBase64(String secretBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        return new SecretKeySpec(keyBytes, DES);
    }

    /**
     * [不安全] 使用 DES/CBC 模式加密数据。
     * <p><b>警告:</b> 此方法极不安全，仅用于与无法升级的遗留系统进行数据交换。
     * 返回的字节数组是IV和密文的简单拼接，这是一个脆弱的设计。
     *
     * @param plaintext 待加密的明文。
     * @param key       DES密钥。
     * @return 加密后的字节数组，格式为 [IV + 密文]。
     * @deprecated 请使用 {@link #encryptAesGcm(byte[], SecretKey)}。
     */
    @Deprecated
    public static byte[] encryptDesCbc(byte[] plaintext, SecretKey key) {
        byte[] iv = new byte[DES_IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(DES_CBC_TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);

            byte[] result = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);
            return result;
        } catch (GeneralSecurityException ex) {
            throw new CryptoException("DES/CBC encryption failed", ex);
        }
    }

    /**
     * [不安全] 使用 DES/CBC 模式解密数据。
     * <p><b>警告:</b> 此方法极不安全，仅用于与无法升级的遗留系统进行数据交换。
     *
     * @param ciphertextWithIv 包含IV的密文 [IV + 密文]。
     * @param key              DES密钥。
     * @return 解密后的明文字节数组。
     * @deprecated 请使用 {@link #decryptAesGcm(SymmetricCiphertext, SecretKey)}。
     */
    @Deprecated
    public static byte[] decryptDesCbc(byte[] ciphertextWithIv, SecretKey key) {
        if (ciphertextWithIv == null || ciphertextWithIv.length <= DES_IV_LENGTH_BYTES) {
            throw new IllegalArgumentException("Invalid encrypted data: shorter than or equal to IV length.");
        }
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(ciphertextWithIv, 0, DES_IV_LENGTH_BYTES);
            Cipher cipher = Cipher.getInstance(DES_CBC_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return cipher.doFinal(ciphertextWithIv, DES_IV_LENGTH_BYTES, ciphertextWithIv.length - DES_IV_LENGTH_BYTES);
        } catch (GeneralSecurityException ex) {
            throw new CryptoException("DES/CBC decryption failed", ex);
        }
    }

    // =================================================================================================================
    // 非对称加密 (Asymmetric Encryption - RSA)
    // =================================================================================================================

    /**
     * 封装所有RSA相关操作的静态内部类，以提供清晰的命名空间。
     */
    public static final class Rsa {

        private static final String RSA_ALGORITHM = "RSA";
        /** RSA/ECB/OAEPWithSHA-256AndMGF1Padding: 推荐的RSA加密填充方案，提供了更强的安全性。*/
        public static final String PADDING_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
        /** RSA/ECB/PKCS1Padding: 传统的填充方案，为兼容旧系统保留。新应用应优先使用OAEP。*/
        public static final String PADDING_PKCS1 = "RSA/ECB/PKCS1Padding";
        /** SHA256withRSA: 推荐的RSA签名算法。*/
        public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

        private Rsa() {}

        /**
         * 生成RSA密钥对。
         *
         * @param keySizeInBits 密钥位数，必须是 2048 或更高。
         * @return 生成的 {@link KeyPair} 对象。
         */
        public static KeyPair genKeyPair(final int keySizeInBits) {
            if (keySizeInBits < 2048) {
                throw new IllegalArgumentException("Unsafe RSA key size. For production use, key size must be at least 2048 bits.");
            }
            try {
                KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
                keyPairGen.initialize(keySizeInBits, SECURE_RANDOM);
                return keyPairGen.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                throw new CryptoException("RSA algorithm not found", e);
            }
        }

        /** @return 公钥的Base64编码字符串 (X.509格式)。 */
        public static String getPublicKeyBase64(final PublicKey publicKey) {
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        }

        /** @return 私钥的Base64编码字符串 (PKCS#8格式)。 */
        public static String getPrivateKeyBase64(final PrivateKey privateKey) {
            return Base64.getEncoder().encodeToString(privateKey.getEncoded());
        }

        /**
         * 从X.509格式的Base64字符串中恢复公钥。
         * @param publicKeyBase64 Base64编码的公钥。
         * @return {@link PublicKey} 对象。
         */
        public static PublicKey getPublicKeyFromBase64(final String publicKeyBase64) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
                return keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new CryptoException("Failed to generate public key from base64 string", e);
            }
        }

        /**
         * 从PKCS#8格式的Base64字符串中恢复私钥。
         * @param privateKeyBase64 Base64编码的私钥。
         * @return {@link PrivateKey} 对象。
         */
        public static PrivateKey getPrivateKeyFromBase64(final String privateKeyBase64) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
                return keyFactory.generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new CryptoException("Failed to generate private key from base64 string", e);
            }
        }

        /**
         * 使用公钥加密数据 (默认使用推荐的 OAEP 填充)。
         * <p><b>注意:</b> RSA不适合加密大数据，通常用于加密对称密钥（数字信封模型）或少量敏感数据。
         * @param data 待加密数据。
         * @param publicKey 公钥。
         * @return 加密后的密文。
         */
        public static byte[] encrypt(final byte[] data, final PublicKey publicKey) {
            return encrypt(data, publicKey, PADDING_OAEP);
        }

        /**
         * 使用公钥加密数据，并指定填充模式。
         * @param data 待加密数据。
         * @param publicKey 公钥。
         * @param padding 填充模式，使用本类中的 PADDING_* 常量。
         * @return 加密后的密文。
         */
        public static byte[] encrypt(final byte[] data, final PublicKey publicKey, final String padding) {
            try {
                Cipher cipher = Cipher.getInstance(padding);
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                return cipher.doFinal(data);
            } catch (GeneralSecurityException e) {
                throw new CryptoException("RSA encryption failed", e);
            }
        }

        /**
         * 使用私钥解密数据 (默认使用与加密对应的 OAEP 填充)。
         * @param data 待解密密文。
         * @param privateKey 私钥。
         * @return 解密后的明文。
         */
        public static byte[] decrypt(final byte[] data, final PrivateKey privateKey) {
            return decrypt(data, privateKey, PADDING_OAEP);
        }

        /**
         * 使用私钥解密数据，并指定填充模式。
         * @param data 待解密密文。
         * @param privateKey 私钥。
         * @param padding 填充模式，使用本类中的 PADDING_* 常量。
         * @return 解密后的明文。
         */
        public static byte[] decrypt(final byte[] data, final PrivateKey privateKey, final String padding) {
            try {
                Cipher cipher = Cipher.getInstance(padding);
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return cipher.doFinal(data);
            } catch (GeneralSecurityException e) {
                throw new CryptoException("RSA decryption failed", e);
            }
        }

        /**
         * 使用私钥对数据进行签名 (使用 SHA256withRSA)。
         * @param data 待签名的数据。
         * @param privateKey 私钥。
         * @return 签名后的字节数组。
         */
        public static byte[] sign(final byte[] data, final PrivateKey privateKey) {
            try {
                Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
                signature.initSign(privateKey);
                signature.update(data);
                return signature.sign();
            } catch (GeneralSecurityException e) {
                throw new CryptoException("RSA signing failed", e);
            }
        }

        /**
         * 使用公钥验证签名。
         * @param data 原始数据。
         * @param signatureBytes 待验证的签名。
         * @param publicKey 公钥。
         * @return 如果签名有效，返回 true；否则返回 false。
         */
        public static boolean verify(final byte[] data, final byte[] signatureBytes, final PublicKey publicKey) {
            try {
                Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
                sig.initVerify(publicKey);
                sig.update(data);
                return sig.verify(signatureBytes);
            } catch (InvalidKeyException | NoSuchAlgorithmException e) {
                throw new CryptoException("RSA verification setup failed", e);
            } catch (SignatureException e) {
                // 签名格式错误或验证失败，这在业务上是正常情况，应返回false，而不是抛出异常。
                return false;
            }
        }
    }
}

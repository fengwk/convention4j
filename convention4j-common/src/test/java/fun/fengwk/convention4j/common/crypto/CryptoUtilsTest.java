package fun.fengwk.convention4j.common.crypto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 对 CryptoUtils 类的全面单元测试，确保其所有功能的正确性、健壮性和安全性。
 *
 * @author AI Assistant
 * @version 1.1 (Fixed incorrect hash constants)
 */
@DisplayName("CryptoUtils 全面单元测试")
class CryptoUtilsTest {

    private static final byte[] SAMPLE_DATA = "This is some sample data for testing.".getBytes(StandardCharsets.UTF_8);
    private static final String SAMPLE_STRING = "Hello, World!";

    @Nested
    @DisplayName("辅助工具测试 (Hex & Safe Compare)")
    class HelperUtilsTests {

        @Test
        @DisplayName("字节数组与十六进制字符串应能成功互转")
        void testBytesHexConversion_shouldSucceed() {
            byte[] original = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF };
            String hex = "deadbeef";

            assertEquals(hex, CryptoUtils.bytes2HexStr(original));
            assertArrayEquals(original, CryptoUtils.hexStr2Bytes(hex));
        }

        @Test
        @DisplayName("十六进制转换应能处理空字节数组")
        void testBytesHexConversion_withEmptyArray() {
            assertEquals("", CryptoUtils.bytes2HexStr(new byte[0]));
            assertArrayEquals(new byte[0], CryptoUtils.hexStr2Bytes(""));
        }

        @Test
        @DisplayName("将奇数长度的十六进制字符串转换为字节应抛出异常")
        void testHexStr2Bytes_withOddLength_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> CryptoUtils.hexStr2Bytes("abc"));
        }

        @Test
        @DisplayName("将包含无效字符的十六进制字符串转换为字节应抛出异常")
        void testHexStr2Bytes_withInvalidChars_shouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> CryptoUtils.hexStr2Bytes("abgz"));
        }

        @Test
        @DisplayName("safeEquals 应能正确比较哈希值")
        void testSafeEquals() {
            String hash1 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // SHA-256 of ""
            String hash2 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
            String hash3 = "f2ca1bb6c7e907d06dafe4687e579fce76b37e4e93b7605022da52e6ccc26fd2"; // SHA-256 of "a"

            assertTrue(CryptoUtils.safeEquals(hash1, hash2));
            assertFalse(CryptoUtils.safeEquals(hash1, hash3));
            assertTrue(CryptoUtils.safeEquals(null, null));
            assertFalse(CryptoUtils.safeEquals(hash1, null));
        }
    }

    @Nested
    @DisplayName("消息摘要 (Hashing) 测试")
    class HashingTests {

        @Test
        @DisplayName("SHA-256 哈希应生成正确的、已知的值")
        void testSha256_shouldProduceKnownValue() {
            // CORRECTED: The actual SHA-256 hash of "Hello, World!"
            String expected = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
            assertEquals(expected, CryptoUtils.sha256Hex(SAMPLE_STRING));
            assertEquals(expected, CryptoUtils.sha256Hex(SAMPLE_STRING.getBytes(StandardCharsets.UTF_8)));
        }

        @Test
        @DisplayName("SHA-256 应能正确处理输入流")
        void testSha256_withInputStream_shouldWorkCorrectly() throws IOException {
            String expected = CryptoUtils.sha256Hex(SAMPLE_DATA);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(SAMPLE_DATA);
            assertEquals(expected, CryptoUtils.sha256Hex(inputStream));
        }

        @Test
        @DisplayName("对 null 输入进行哈希应抛出 NullPointerException")
        void testSha256_withNullInput_shouldThrowException() {
            assertThrows(NullPointerException.class, () -> CryptoUtils.sha256Hex((String) null));
            assertThrows(NullPointerException.class, () -> CryptoUtils.sha256Hex((byte[]) null));
        }
    }

    @Nested
    @DisplayName("消息认证码 (HMAC) 测试")
    class MacTests {

        @Test
        @DisplayName("HMAC-SHA256 应在数据和密钥相同时生成一致的 MAC")
        void testHmacSha256_shouldBeConsistent() {
            SecretKey key = CryptoUtils.genAesKey(256); // Any secret key is fine for HMAC
            String mac1 = CryptoUtils.hmacSha256Hex(SAMPLE_DATA, key);
            String mac2 = CryptoUtils.hmacSha256Hex(SAMPLE_DATA, key);
            assertTrue(CryptoUtils.safeEquals(mac1, mac2));
        }

        @Test
        @DisplayName("HMAC-SHA256 在数据被篡改后应生成不同的 MAC")
        void testHmacSha256_withTamperedData_shouldProduceDifferentMac() {
            SecretKey key = CryptoUtils.genAesKey(256);
            String originalMac = CryptoUtils.hmacSha256Hex(SAMPLE_DATA, key);

            byte[] tamperedData = SAMPLE_DATA.clone();
            tamperedData[0] ^= 0x01; // Flip a bit

            String tamperedMac = CryptoUtils.hmacSha256Hex(tamperedData, key);
            assertFalse(CryptoUtils.safeEquals(originalMac, tamperedMac));
        }

        @Test
        @DisplayName("HMAC-SHA256 使用不同密钥应生成不同的 MAC")
        void testHmacSha256_withDifferentKey_shouldProduceDifferentMac() {
            SecretKey key1 = CryptoUtils.genAesKey(256);
            SecretKey key2 = CryptoUtils.genAesKey(256);
            String mac1 = CryptoUtils.hmacSha256Hex(SAMPLE_DATA, key1);
            String mac2 = CryptoUtils.hmacSha256Hex(SAMPLE_DATA, key2);
            assertFalse(CryptoUtils.safeEquals(mac1, mac2));
        }
    }

    @Nested
    @DisplayName("对称加密 - AES/GCM (推荐)")
    class AesGcmTests {

        @ParameterizedTest
        @ValueSource(ints = {128, 192, 256})
        @DisplayName("AES/GCM 加解密往返应得到原始数据")
        void testEncryptDecrypt_roundtrip_shouldSucceed(int keySize) {
            SecretKey key = CryptoUtils.genAesKey(keySize);
            CryptoUtils.SymmetricCiphertext encrypted = CryptoUtils.encryptAesGcm(SAMPLE_DATA, key);
            byte[] decrypted = CryptoUtils.decryptAesGcm(encrypted, key);
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("AES/GCM Base64 序列化与反序列化应能正常工作")
        void testBase64Serialization_roundtrip_shouldSucceed() {
            SecretKey key = CryptoUtils.genAesKey(256);
            CryptoUtils.SymmetricCiphertext originalEncrypted = CryptoUtils.encryptAesGcm(SAMPLE_DATA, key);

            String base64 = originalEncrypted.toBase64();
            CryptoUtils.SymmetricCiphertext deserialized = CryptoUtils.SymmetricCiphertext.fromBase64Gcm(base64);

            assertEquals(originalEncrypted, deserialized);

            byte[] decrypted = CryptoUtils.decryptAesGcm(deserialized, key);
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("使用错误的密钥解密 GCM 数据应抛出异常")
        void testDecrypt_withWrongKey_shouldThrowException() {
            SecretKey key1 = CryptoUtils.genAesKey(256);
            SecretKey key2 = CryptoUtils.genAesKey(256);
            CryptoUtils.SymmetricCiphertext encrypted = CryptoUtils.encryptAesGcm(SAMPLE_DATA, key1);

            CryptoUtils.CryptoException ex = assertThrows(CryptoUtils.CryptoException.class,
                () -> CryptoUtils.decryptAesGcm(encrypted, key2));
            // GCM的认证标签失败会抛出此异常
            assertInstanceOf(AEADBadTagException.class, ex.getCause());
        }

        @Test
        @DisplayName("解密被篡改的 GCM 密文应抛出异常")
        void testDecrypt_withTamperedCiphertext_shouldThrowException() {
            SecretKey key = CryptoUtils.genAesKey(256);
            CryptoUtils.SymmetricCiphertext encrypted = CryptoUtils.encryptAesGcm(SAMPLE_DATA, key);

            // 篡改密文
            byte[] tamperedCiphertextBytes = encrypted.getCiphertext();
            tamperedCiphertextBytes[tamperedCiphertextBytes.length - 1] ^= 0x01;

            String tamperedBase64 = new CryptoUtils.SymmetricCiphertext(encrypted.getIv(), tamperedCiphertextBytes).toBase64();
            CryptoUtils.SymmetricCiphertext tamperedEncrypted = CryptoUtils.SymmetricCiphertext.fromBase64Gcm(tamperedBase64);

            assertThrows(CryptoUtils.CryptoException.class, () -> CryptoUtils.decryptAesGcm(tamperedEncrypted, key));
        }

    }

    @Nested
    @DisplayName("对称加密 - AES/CBC (兼容遗留)")
    class AesCbcTests {

        @ParameterizedTest
        @ValueSource(ints = {128, 192, 256})
        @DisplayName("AES/CBC 加解密往返应得到原始数据")
        void testEncryptDecrypt_roundtrip_shouldSucceed(int keySize) {
            SecretKey key = CryptoUtils.genAesKey(keySize);
            CryptoUtils.SymmetricCiphertext encrypted = CryptoUtils.encryptAesCbc(SAMPLE_DATA, key);
            byte[] decrypted = CryptoUtils.decryptAesCbc(encrypted, key);
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("AES/CBC Base64 序列化与反序列化应能正常工作")
        void testBase64Serialization_roundtrip_shouldSucceed() {
            SecretKey key = CryptoUtils.genAesKey(256);
            CryptoUtils.SymmetricCiphertext originalEncrypted = CryptoUtils.encryptAesCbc(SAMPLE_DATA, key);

            String base64 = originalEncrypted.toBase64();
            CryptoUtils.SymmetricCiphertext deserialized = CryptoUtils.SymmetricCiphertext.fromBase64Cbc(base64);

            assertEquals(originalEncrypted, deserialized);

            byte[] decrypted = CryptoUtils.decryptAesCbc(deserialized, key);
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("使用错误的密钥解密 CBC 数据应抛出异常")
        void testDecrypt_withWrongKey_shouldThrowException() {
            SecretKey key1 = CryptoUtils.genAesKey(256);
            SecretKey key2 = CryptoUtils.genAesKey(256);
            CryptoUtils.SymmetricCiphertext encrypted = CryptoUtils.encryptAesCbc(SAMPLE_DATA, key1);

            // 错误的密钥通常会导致填充错误
            assertThrows(CryptoUtils.CryptoException.class,
                () -> CryptoUtils.decryptAesCbc(encrypted, key2));
        }
    }

    @Nested
    @DisplayName("非对称加密 - RSA")
    class RsaTests {

        @Test
        @DisplayName("RSA 密钥对生成器应能正常工作且密钥长度符合要求")
        void testKeyPairGeneration() {
            assertDoesNotThrow(() -> CryptoUtils.Rsa.genKeyPair(2048));
            assertThrows(IllegalArgumentException.class, () -> CryptoUtils.Rsa.genKeyPair(1024));
        }

        @Test
        @DisplayName("RSA 密钥的 Base64 序列化和反序列化应能正常工作")
        void testKeySerialization_roundtrip() {
            KeyPair keyPair = CryptoUtils.Rsa.genKeyPair(2048);
            PublicKey originalPublic = keyPair.getPublic();
            PrivateKey originalPrivate = keyPair.getPrivate();

            String pubKeyBase64 = CryptoUtils.Rsa.getPublicKeyBase64(originalPublic);
            String privKeyBase64 = CryptoUtils.Rsa.getPrivateKeyBase64(originalPrivate);

            PublicKey restoredPublic = CryptoUtils.Rsa.getPublicKeyFromBase64(pubKeyBase64);
            PrivateKey restoredPrivate = CryptoUtils.Rsa.getPrivateKeyFromBase64(privKeyBase64);

            assertEquals(originalPublic, restoredPublic);
            assertEquals(originalPrivate, restoredPrivate);
        }

        @Test
        @DisplayName("RSA/OAEP 加解密往返应得到原始数据")
        void testEncryptDecrypt_roundtrip_OAEP() {
            KeyPair keyPair = CryptoUtils.Rsa.genKeyPair(2048);
            byte[] encrypted = CryptoUtils.Rsa.encrypt(SAMPLE_DATA, keyPair.getPublic());
            byte[] decrypted = CryptoUtils.Rsa.decrypt(encrypted, keyPair.getPrivate());
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("RSA/PKCS1 加解密往返应得到原始数据")
        void testEncryptDecrypt_roundtrip_PKCS1() {
            KeyPair keyPair = CryptoUtils.Rsa.genKeyPair(2048);
            byte[] encrypted = CryptoUtils.Rsa.encrypt(SAMPLE_DATA, keyPair.getPublic(), CryptoUtils.Rsa.PADDING_PKCS1);
            byte[] decrypted = CryptoUtils.Rsa.decrypt(encrypted, keyPair.getPrivate(), CryptoUtils.Rsa.PADDING_PKCS1);
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("RSA 签名与验证往返应成功")
        void testSignVerify_roundtrip_shouldSucceed() {
            KeyPair keyPair = CryptoUtils.Rsa.genKeyPair(2048);
            byte[] signature = CryptoUtils.Rsa.sign(SAMPLE_DATA, keyPair.getPrivate());
            boolean isValid = CryptoUtils.Rsa.verify(SAMPLE_DATA, signature, keyPair.getPublic());
            assertTrue(isValid);
        }

        @Test
        @DisplayName("使用被篡改的数据进行签名验证应失败")
        void testVerify_withTamperedData_shouldFail() {
            KeyPair keyPair = CryptoUtils.Rsa.genKeyPair(2048);
            byte[] signature = CryptoUtils.Rsa.sign(SAMPLE_DATA, keyPair.getPrivate());

            byte[] tamperedData = SAMPLE_DATA.clone();
            tamperedData[0] ^= 0x01; // Flip a bit

            boolean isValid = CryptoUtils.Rsa.verify(tamperedData, signature, keyPair.getPublic());
            assertFalse(isValid);
        }

        @Test
        @DisplayName("使用错误的公钥进行签名验证应失败")
        void testVerify_withWrongPublicKey_shouldFail() {
            KeyPair keyPair1 = CryptoUtils.Rsa.genKeyPair(2048);
            KeyPair keyPair2 = CryptoUtils.Rsa.genKeyPair(2048);
            byte[] signature = CryptoUtils.Rsa.sign(SAMPLE_DATA, keyPair1.getPrivate());
            boolean isValid = CryptoUtils.Rsa.verify(SAMPLE_DATA, signature, keyPair2.getPublic());
            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("遗留与不安全算法测试")
    class LegacyAlgorithmTests {

        @Test
        @DisplayName("MD5 哈希应生成正确的、已知的值")
        void testMd5_shouldProduceKnownValue() {
            // MD5 of "Hello, World!"
            String expected = "65a8e27d8879283831b664bd8b7f0ad4";
            assertEquals(expected, CryptoUtils.md5Hex(SAMPLE_STRING));
        }

        @Test
        @DisplayName("SHA-1 哈希应生成正确的、已知的值")
        void testSha1_shouldProduceKnownValue() {
            // CORRECTED: The actual SHA-1 hash of "Hello, World!"
            String expected = "0a0a9f2a6772942557ab5355d76af442f8f65e01";
            assertEquals(expected, CryptoUtils.sha1Hex(SAMPLE_STRING));
        }

        @Test
        @DisplayName("对 null 输入进行遗留哈希应抛出 NullPointerException")
        void testLegacyHash_withNullInput_shouldThrowException() {
            assertThrows(NullPointerException.class, () -> CryptoUtils.md5Hex(null));
            assertThrows(NullPointerException.class, () -> CryptoUtils.sha1Hex(null));
        }

        @Test
        @DisplayName("DES/CBC 加解密往返应得到原始数据 (仅为兼容性测试)")
        void testDesCbc_roundtrip_shouldSucceedForCompatibility() {
            SecretKey key = CryptoUtils.genDesKey();
            byte[] encrypted = CryptoUtils.encryptDesCbc(SAMPLE_DATA, key);
            byte[] decrypted = CryptoUtils.decryptDesCbc(encrypted, key);
            assertArrayEquals(SAMPLE_DATA, decrypted);
        }

        @Test
        @DisplayName("从 Base64 恢复 DES 密钥应能正常工作")
        void testDesKeyFromBase64() {
            SecretKey key = CryptoUtils.genDesKey();
            String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
            SecretKey restoredKey = CryptoUtils.getDesKeyFromBase64(base64Key);
            assertEquals(key, restoredKey);
        }
    }
}

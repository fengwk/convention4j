package fun.fengwk.convention4j.common.http.client;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 为 LineStreamBodyDecoder 编写的完整且健壮的单元测试。
 * 覆盖了基本功能、边界情况、长文本处理和异常输入等多种场景。
 */
class LineStreamBodyDecoderTest {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final Charset GBK = Charset.forName("GBK");

    @Test
    void testBasicDecoding_UTF8_LF() throws CharacterCodingException {
        // 场景：基本解码，一次性接收所有数据块
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\nline2\nline3\n";
        List<ByteBuffer> chunks = splitIntoChunks(text, UTF_8, 5);
        // 1. 一次性追加所有数据块
        for (ByteBuffer chunk : chunks) {
            decoder.appendChunk(chunk);
        }
        // 2. 验证可以依次取出所有行
        assertEquals("line1", decoder.nextLine());
        assertEquals("line2", decoder.nextLine());
        assertEquals("line3", decoder.nextLine()); // <-- 修正点 1: 这里应该返回 "line3"
        // 3. 此时缓冲区已空，再次调用 nextLine 应该返回 null
        assertNull(decoder.nextLine()); // <-- 修正点 2: 这时才应该是 null
        // 4. 调用 finish 时，因为没有剩余数据，应该返回空列表
        List<String> remaining = decoder.finish();
        assertTrue(remaining.isEmpty()); // <-- 修正点 3: 应该为空
    }

    @Test
    void testIntermittentChunkProcessing() throws CharacterCodingException {
        // 场景：模拟流式处理，边接收数据边处理行
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\nline2\nline3"; // 注意，最后一行没有换行符
        List<ByteBuffer> chunks = splitIntoChunks(text, UTF_8, 6);
        // chunks: ["line1\n", "line2\n", "line3"]
        // 接收第一个 chunk，包含 "line1\n"
        decoder.appendChunk(chunks.get(0));
        assertEquals("line1", decoder.nextLine());
        // 此时没有更多完整的行
        assertNull(decoder.nextLine());
        // 接收第二个 chunk，包含 "line2\n"
        decoder.appendChunk(chunks.get(1));
        assertEquals("line2", decoder.nextLine());
        // 再次检查，没有更多完整的行
        assertNull(decoder.nextLine());
        // 接收第三个 chunk，只包含 "line3"，没有换行符
        decoder.appendChunk(chunks.get(2));
        // 因为没有换行符，nextLine 仍然返回 null
        assertNull(decoder.nextLine());
        // 调用 finish，应该返回最后不完整的行
        List<String> remaining = decoder.finish();
        assertEquals(1, remaining.size());
        assertEquals("line3", remaining.get(0));
    }

    @Test
    void testDecodingWith_CRLF_and_CR() throws CharacterCodingException {
        // 场景：测试对 \r\n 和 \r 作为换行符的默认支持
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\r\nline2\rline3\n";
        List<ByteBuffer> chunks = splitIntoChunks(text, UTF_8, 6);

        for (ByteBuffer chunk : chunks) {
            decoder.appendChunk(chunk);
        }

        assertEquals("line1", decoder.nextLine());
        assertEquals("line2", decoder.nextLine());

        List<String> remaining = decoder.finish();
        assertEquals(1, remaining.size());
        assertEquals("line3", remaining.get(0));
    }

    @Test
    void testDecodingWithCustomEOL() throws CharacterCodingException {
        // 场景：使用自定义的行分隔符
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder("_EOL_", UTF_8);
        String text = "data1_EOL_data2_EOL_";
        List<ByteBuffer> chunks = splitIntoChunks(text, UTF_8, 7);

        for (ByteBuffer chunk : chunks) {
            decoder.appendChunk(chunk);
        }

        assertEquals("data1", decoder.nextLine());

        List<String> remaining = decoder.finish();
        assertEquals(1, remaining.size());
        assertEquals("data2", remaining.get(0));
    }

    @Test
    void testMultiByteCharacterSplitAcrossChunks_GBK() throws CharacterCodingException, UnsupportedEncodingException {
        // 场景：一个多字节字符（中文）被切分到两个 ByteBuffer 中
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(GBK);

        String text = "你好\n世界";
        byte[] bytes = text.getBytes(GBK);

        // "你好" 的 GBK 编码是 [C4 E3 BA C3]，我们在这里切开
        ByteBuffer chunk1 = ByteBuffer.wrap(bytes, 0, 3); // "你" 和 "好" 的第一个字节
        ByteBuffer chunk2 = ByteBuffer.wrap(bytes, 3, bytes.length - 3); // "好" 的第二个字节和后面的内容

        decoder.appendChunk(chunk1);
        // 此时 "你" 应该被解码，但 "好" 的一半在 pending buffer 中，所以 nextLine 返回 null
        assertNull(decoder.nextLine());

        decoder.appendChunk(chunk2);

        assertEquals("你好", decoder.nextLine());

        List<String> remaining = decoder.finish();
        assertEquals(1, remaining.size());
        assertEquals("世界", remaining.get(0));
    }

    @Test
    void testEOLSplitAcrossChunks() throws CharacterCodingException {
        // 场景：换行符 \r\n 被切分到两个 ByteBuffer 中
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\r\nline2";
        byte[] bytes = text.getBytes(UTF_8);

        int splitIndex = text.indexOf('\r') + 1; // 在 \r 之后切分
        ByteBuffer chunk1 = ByteBuffer.wrap(bytes, 0, splitIndex);
        ByteBuffer chunk2 = ByteBuffer.wrap(bytes, splitIndex, bytes.length - splitIndex);

        decoder.appendChunk(chunk1);
        // 此时 "line1\r" 在缓冲区，但不是完整的行
        assertNull(decoder.nextLine());

        decoder.appendChunk(chunk2);

        assertEquals("line1", decoder.nextLine());

        List<String> remaining = decoder.finish();
        assertEquals(1, remaining.size());
        assertEquals("line2", remaining.get(0));
    }

    @Test
    void testNoTrailingEOL() throws CharacterCodingException {
        // 场景：输入的数据最后没有换行符
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\nline2"; // line2 后面没有 \n
        decoder.appendChunk(ByteBuffer.wrap(text.getBytes(UTF_8)));

        assertEquals("line1", decoder.nextLine());
        assertNull(decoder.nextLine()); // line2 还在缓冲区，但不是完整的行

        List<String> remaining = decoder.finish();
        assertEquals(1, remaining.size());
        assertEquals("line2", remaining.get(0));
    }

    @Test
    void testEmptyLines() throws CharacterCodingException {
        // 场景：输入包含空行
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\n\nline3\n";
        decoder.appendChunk(ByteBuffer.wrap(text.getBytes(UTF_8)));

        assertEquals("line1", decoder.nextLine());
        assertEquals("", decoder.nextLine()); // 第二行是空字符串
        assertEquals("line3", decoder.nextLine());
        assertNull(decoder.nextLine());

        assertTrue(decoder.finish().isEmpty());
    }

    @Test
    void testFinishMethodWithRemainingBuffer() throws CharacterCodingException {
        // 场景：缓冲区有几行数据，直接调用 finish
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        String text = "line1\nline2\nline3";
        decoder.appendChunk(ByteBuffer.wrap(text.getBytes(UTF_8)));

        List<String> allLines = decoder.finish();

        assertEquals(3, allLines.size());
        assertEquals("line1", allLines.get(0));
        assertEquals("line2", allLines.get(1));
        assertEquals("line3", allLines.get(2));
    }

    @Test
    void testCallingMethodsAfterFinished() throws CharacterCodingException {
        // 场景：在 finish() 后再次调用方法，应抛出异常
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        decoder.appendChunk(ByteBuffer.wrap("hello".getBytes(UTF_8)));
        decoder.finish();

        assertThrows(IllegalArgumentException.class, decoder::finish);
        assertThrows(IllegalArgumentException.class, () ->
            decoder.appendChunk(ByteBuffer.wrap("world".getBytes(UTF_8)))
        );
        assertThrows(IllegalArgumentException.class, decoder::nextLine);
    }

    @Test
    void testEmptyInputAndEmptyChunks() throws CharacterCodingException {
        // 场景：没有输入或输入空 chunk
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);

        decoder.appendChunk(null); // 测试null chunk
        decoder.appendChunk(ByteBuffer.allocate(0)); // 测试空 chunk

        assertNull(decoder.nextLine());

        List<String> lines = decoder.finish();
        assertTrue(lines.isEmpty());
    }

    @Test
    void testLongTextExceedingInternalBuffer() throws CharacterCodingException {
        // 场景：处理超过内部缓冲区大小（8K）的长文本
        LineStreamBodyDecoder decoder = new LineStreamBodyDecoder(UTF_8);
        final int internalCharBufferSize = 8 * 1024;

        // Part 1: 测试单行文本长度超过内部缓冲区
        String longLineContent = "a".repeat(internalCharBufferSize + 2000);
        String longLineText = longLineContent + "\n";
        decoder.appendChunk(ByteBuffer.wrap(longLineText.getBytes(UTF_8)));
        String resultLine = decoder.nextLine();
        assertNotNull(resultLine);
        assertEquals(longLineContent.length(), resultLine.length());
        assertEquals(longLineContent, resultLine);
        assertNull(decoder.nextLine());

        // Part 2: 测试包含多行的超大文本块
        StringBuilder massiveTextBuilder = new StringBuilder();
        int lineCount = 1000;
        for (int i = 0; i < lineCount; i++) {
            massiveTextBuilder.append("This is line number ").append(i).append("\n");
        }
        decoder.appendChunk(ByteBuffer.wrap(massiveTextBuilder.toString().getBytes(UTF_8)));
        for (int i = 0; i < lineCount; i++) {
            String expectedLine = "This is line number " + i;
            String actualLine = decoder.nextLine();
            assertNotNull(actualLine, "Expected line " + i + " but got null");
            assertEquals(expectedLine, actualLine);
        }
        assertNull(decoder.nextLine());
        assertTrue(decoder.finish().isEmpty());
    }

    @Test
    void testBoundaryTextScenarios() throws CharacterCodingException {
        // 场景：测试各种边界文本，包括 Emoji、格式错误的字节等

        // 场景1: Unicode 代理对 (Emoji) 在数据块中间被切分
        LineStreamBodyDecoder emojiDecoder = new LineStreamBodyDecoder(UTF_8);
        String textWithEmoji = "start-👍-end\nnext";
        byte[] emojiBytes = textWithEmoji.getBytes(UTF_8);
        int splitIndex = textWithEmoji.indexOf("👍") + 2;
        ByteBuffer chunk1 = ByteBuffer.wrap(emojiBytes, 0, splitIndex);
        ByteBuffer chunk2 = ByteBuffer.wrap(emojiBytes, splitIndex, emojiBytes.length - splitIndex);
        emojiDecoder.appendChunk(chunk1);
        assertNull(emojiDecoder.nextLine());
        emojiDecoder.appendChunk(chunk2);
        assertEquals("start-👍-end", emojiDecoder.nextLine());
        List<String> remainingEmoji = emojiDecoder.finish();
        assertEquals(1, remainingEmoji.size());
        assertEquals("next", remainingEmoji.get(0));

        // 场景2: 处理格式错误的 UTF-8 字节序列
        LineStreamBodyDecoder malformedDecoder = new LineStreamBodyDecoder(UTF_8);
        byte[] malformedBytes = new byte[] { 'h', 'e', 'l', 'l', 'o', (byte) 0xE4, (byte) 0xBD, 'A', '\n' };
        malformedDecoder.appendChunk(ByteBuffer.wrap(malformedBytes));
        List<String> malformedResult = malformedDecoder.finish();
        assertEquals(1, malformedResult.size());
        assertEquals("hello\uFFFDA", malformedResult.get(0));

        // 场景3: 文本内容与自定义分隔符部分重叠
        String customEol = "##END##";
        LineStreamBodyDecoder customEolDecoder = new LineStreamBodyDecoder(customEol, UTF_8);
        String trickyText = "data1##END##data2##EN";
        customEolDecoder.appendChunk(ByteBuffer.wrap(trickyText.getBytes(UTF_8)));
        assertEquals("data1", customEolDecoder.nextLine());
        assertNull(customEolDecoder.nextLine());
        List<String> remainingCustom = customEolDecoder.finish();
        assertEquals(1, remainingCustom.size());
        assertEquals("data2##EN", remainingCustom.get(0));
    }

    /**
     * 辅助方法：将字符串按指定块大小切分为多个 ByteBuffer。
     */
    private List<ByteBuffer> splitIntoChunks(String text, Charset charset, int chunkSize) {
        byte[] bytes = text.getBytes(charset);
        List<ByteBuffer> chunks = new ArrayList<>();
        int offset = 0;
        while (offset < bytes.length) {
            int length = Math.min(chunkSize, bytes.length - offset);
            ByteBuffer chunk = ByteBuffer.wrap(Arrays.copyOfRange(bytes, offset, offset + length));
            chunks.add(chunk);
            offset += length;
        }
        return chunks;
    }
}

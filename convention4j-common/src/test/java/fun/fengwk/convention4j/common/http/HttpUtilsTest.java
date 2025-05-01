package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class HttpUtilsTest {

    @Test
    public void testParseContentTypeCharset() {
        Charset charset = HttpUtils.parseContentTypeCharset("application/json;charset=utf-8");
        Assertions.assertEquals(StandardCharsets.UTF_8, charset);
    }

    @Test
    public void testParseKeyValue() {
        Pair<String, String> keyValue = HttpUtils.parseKeyValue("abc = cc");
        Assertions.assertNotNull(keyValue);
        Assertions.assertEquals("abc", keyValue.getKey());
        Assertions.assertEquals("cc", keyValue.getValue());
    }

    @Test
    public void testEncodeUrlComponent() {
        String s = "as d+as*~dae!'d";
        String encoded = HttpUtils.encodeUrlComponent(s, StandardCharsets.UTF_8);
        System.out.println(encoded);
        String decoded = HttpUtils.decodeUrlComponent(encoded, StandardCharsets.UTF_8);
        Assertions.assertEquals(s, decoded);
    }

}

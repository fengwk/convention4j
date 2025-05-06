package fun.fengwk.convention4j.common.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author fengwk
 */
public class FormUrlencodedBuilder extends AbstractUrlencodedBuilder {

    @Override
    protected String urlEncode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

}

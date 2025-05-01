package fun.fengwk.convention4j.common.http;

import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author fengwk
 */
public class FormUrlencodedBuilder extends AbstractUrlencodedBuilder {

    @Override
    protected String urlEncode(String text, Charset charset) {
        return URLEncoder.encode(text, charset);
    }

}

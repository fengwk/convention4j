package fun.fengwk.convention4j.common.http;

import java.nio.charset.Charset;

/**
 * 参数构建器
 *
 * @author fengwk
 */
public class ParametersBuilder extends AbstractUrlencodedBuilder {

    @Override
    protected String urlEncode(String text, Charset charset) {
        return HttpUtils.encodeUrlComponent(text, charset);
    }

}

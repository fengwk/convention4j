package fun.fengwk.convention4j.common.http;

/**
 * 参数构建器
 *
 * @author fengwk
 */
public class ParametersBuilder extends AbstractUrlencodedBuilder {

    @Override
    protected String urlEncode(String text) {
        return HttpUtils.encodeUrlComponent(text);
    }

}

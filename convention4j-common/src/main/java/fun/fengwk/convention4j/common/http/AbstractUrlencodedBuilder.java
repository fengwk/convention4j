package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwk
 */
public abstract class AbstractUrlencodedBuilder {

    private final List<Pair<String, String>> parameters = new ArrayList<>();

    protected abstract String urlEncode(String text);

    public void setParameter(String name, String value) {
        parameters.add(Pair.of(name, value));
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                sb.append('&');
            }
            Pair<String, String> parameter = parameters.get(i);
            String encodedKey = urlEncode(parameter.getKey());
            String encodedValue = urlEncode(parameter.getValue());
            sb.append(encodedKey).append("=").append(encodedValue);
        }
        return sb.toString();
    }

}

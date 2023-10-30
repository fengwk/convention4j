package fun.fengwk.convention4j.common.validation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Map<String, Object> messageParameters;
    private final List<String> propertyNodes;

    public ValidationException(String messageTemplate) {
        this(messageTemplate, Collections.emptyMap(), Collections.emptyList());
    }

    public ValidationException(String messageTemplate, Map<String, Object> messageParameters, List<String> propertyNodes) {
        super(messageTemplate);
        this.messageParameters = messageParameters;
        this.propertyNodes = propertyNodes;
    }

    public ValidationException(String messageTemplate, Throwable cause) {
        this(messageTemplate, Collections.emptyMap(), Collections.emptyList(), cause);
    }

    public ValidationException(String messageTemplate, Map<String, Object> messageParameters, List<String> propertyNodes, Throwable cause) {
        super(messageTemplate, cause);
        this.messageParameters = messageParameters;
        this.propertyNodes = propertyNodes;
    }

    public ValidationException(String messageTemplate, Map<String, Object> messageParameters) {
        this(messageTemplate, messageParameters, Collections.emptyList());
    }

    public ValidationException(String messageTemplate, Map<String, Object> messageParameters, Throwable cause) {
        this(messageTemplate, messageParameters, Collections.emptyList(), cause);
    }

    public ValidationException(String messageTemplate, String propertyNode) {
        this(messageTemplate, Collections.emptyMap(), Collections.singletonList(propertyNode));
    }

    public ValidationException(String messageTemplate, String propertyNode, Throwable cause) {
        this(messageTemplate, Collections.emptyMap(), Collections.singletonList(propertyNode), cause);
    }

    public ValidationException(String messageTemplate, Map<String, Object> messageParameters, String propertyNode) {
        this(messageTemplate, messageParameters, Collections.singletonList(propertyNode));
    }

    public ValidationException(String messageTemplate, Map<String, Object> messageParameters, String propertyNode, Throwable cause) {
        this(messageTemplate, messageParameters, Collections.singletonList(propertyNode), cause);
    }

    public Map<String, Object> getMessageParameters() {
        return messageParameters;
    }

    public List<String> getPropertyNodes() {
        return propertyNodes;
    }

}

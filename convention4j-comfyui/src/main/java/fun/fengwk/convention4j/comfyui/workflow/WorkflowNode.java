package fun.fengwk.convention4j.comfyui.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

/**
 * 工作流节点
 *
 * @author fengwk
 */
@Getter
@ToString
@RequiredArgsConstructor
public class WorkflowNode {
    private final String id;
    private final String classType;
    private final ObjectNode inputs;
    private final JsonNode meta;

    /**
     * 获取输入值
     */
    public JsonNode getInput(String name) {
        return inputs.path(name);
    }

    /**
     * 设置输入值
     */
    public WorkflowNode setInput(String name, String value) {
        inputs.put(name, value);
        return this;
    }

    /**
     * 设置输入值（数值）
     */
    public WorkflowNode setInput(String name, long value) {
        inputs.put(name, value);
        return this;
    }

    /**
     * 设置输入值（JsonNode）
     */
    public WorkflowNode setInput(String name, JsonNode value) {
        inputs.set(name, value);
        return this;
    }

    /**
     * 设置输入值（通用对象）
     * 自动将对象转换为合适的 JsonNode 类型
     */
    public WorkflowNode setInput(String name, Object value) {
        if (value == null) {
            inputs.putNull(name);
        } else if (value instanceof String) {
            return setInput(name, (String) value);
        } else if (value instanceof Long) {
            return setInput(name, ((Long) value).longValue());
        } else if (value instanceof Integer) {
            return setInput(name, ((Integer) value).longValue());
        } else if (value instanceof JsonNode) {
            return setInput(name, (JsonNode) value);
        } else {
            // 将其他类型转换为 JsonNode
            JsonNode node = fun.fengwk.convention4j.common.json.jackson.JacksonUtils.valueToTree(value);
            if (node != null) {
                inputs.set(name, node);
            }
        }
        return this;
    }

    /**
     * 判断输入是否为链接
     * 在API格式中，链接通常表示为列表 [nodeId, outputIndex]
     */
    public boolean isLinkedInput(String name) {
        JsonNode value = inputs.path(name);
        return value.isArray() && value.size() == 2
            && value.get(0).isTextual() && value.get(1).isNumber();
    }

    /**
     * 获取链接信息
     */
    public Optional<NodeLink> getLink(String name) {
        if (!isLinkedInput(name)) {
            return Optional.empty();
        }
        JsonNode list = inputs.path(name);
        String sourceNodeId = list.get(0).asText();
        int sourceOutputIndex = list.get(1).asInt();
        return Optional.of(new NodeLink(id, name, sourceNodeId, sourceOutputIndex));
    }

    /**
     * 获取输入值的集合（兼容旧代码）
     */
    public ObjectNode getInputs() {
        return inputs;
    }

    /**
     * 获取元信息
     */
    public JsonNode getMeta() {
        return meta;
    }
}
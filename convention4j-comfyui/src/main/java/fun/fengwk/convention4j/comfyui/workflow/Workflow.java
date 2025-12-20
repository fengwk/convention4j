package fun.fengwk.convention4j.comfyui.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import fun.fengwk.convention4j.comfyui.exception.WorkflowException;
import fun.fengwk.convention4j.common.json.jackson.JacksonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流定义
 *
 * @author fengwk
 */
@Slf4j
public class Workflow {

    // 支持的seed字段名称列表，按优先级排序
    private static final List<String> SEED_FIELD_NAMES = List.of(
            ComfyUIConstants.InputFields.SEED,
            ComfyUIConstants.InputFields.NOISE_SEED,
            ComfyUIConstants.InputFields.CTRL_SEED
    );

    private final Map<String, WorkflowNode> nodes = new LinkedHashMap<>();

    private Workflow() {}

    /**
     * 从API格式的JSON创建工作流
     */
    public static Workflow fromApiJson(String apiJson) {
        Workflow workflow = new Workflow();
        try {
            JsonNode rawNodes = JacksonUtils.readTree(apiJson);
            if (rawNodes == null || !rawNodes.isObject()) {
                throw new WorkflowException("Invalid API JSON format");
            }
            
            Iterator<Map.Entry<String, JsonNode>> fields = rawNodes.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String id = entry.getKey();
                JsonNode data = entry.getValue();
                
                String classType = data.path(ComfyUIConstants.JsonFields.CLASS_TYPE).asText();
                JsonNode inputsNode = data.path(ComfyUIConstants.JsonFields.INPUTS);
                JsonNode metaNode = data.path(ComfyUIConstants.JsonFields.META);
                
                // 确保inputs是ObjectNode
                ObjectNode inputs = inputsNode.isObject() ? (ObjectNode) inputsNode
                    : JsonNodeFactory.instance.objectNode();
                
                workflow.nodes.put(id, new WorkflowNode(id, classType, inputs, metaNode));
            }
        } catch (WorkflowException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse API JSON: {}", apiJson, e);
            throw new WorkflowException("Failed to parse API JSON", e);
        }
        return workflow;
    }

    /**
     * 从普通格式的JSON创建工作流
     * 自动转换为API格式
     */
    public static Workflow fromJson(String json) {
        JsonNode root = JacksonUtils.readTree(json);
        if (root != null && root.has(ComfyUIConstants.JsonFields.NODES) && root.has(ComfyUIConstants.JsonFields.LINKS)) {
            throw new WorkflowException("Please use 'Save (API Format)' in ComfyUI to export the workflow JSON.");
        }
        return fromApiJson(json);
    }

    /**
     * 获取节点
     */
    public WorkflowNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * 按类型获取节点列表
     */
    public List<WorkflowNode> getNodesByType(String classType) {
        return nodes.values().stream()
                .filter(node -> classType.equals(node.getClassType()))
                .collect(Collectors.toList());
    }

    /**
     * 按类型查找唯一节点
     */
    public Optional<WorkflowNode> findNodeByType(String classType) {
        List<WorkflowNode> found = getNodesByType(classType);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        if (found.size() > 1) {
            log.warn("Found multiple nodes of type {}, returning first one", classType);
        }
        return Optional.of(found.get(0));
    }

    /**
     * 获取所有节点ID
     */
    public Set<String> getNodeIds() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    /**
     * 设置节点属性
     */
    public Workflow setProperty(String nodeId, String path, Object value) {
        WorkflowNode node = getNode(nodeId);
        if (node == null) {
            throw new WorkflowException("Node not found: " + nodeId);
        }
        
        // 简单路径支持：目前只支持直接设置inputs下的属性
        // path example: "inputs/seed" -> 设置inputs map中的seed
        // 如果 path 包含 /，则进行分割
        
        if (path.startsWith(ComfyUIConstants.JsonFields.INPUTS + "/")) {
            String inputName = path.substring((ComfyUIConstants.JsonFields.INPUTS + "/").length());
            node.setInput(inputName, value);
        } else {
            // 默认设置到inputs中，或者根据实际需求扩展
            node.setInput(path, value);
        }
        return this;
    }

    /**
     * 获取节点属性
     */
    public Object getProperty(String nodeId, String path) {
        WorkflowNode node = getNode(nodeId);
        if (node == null) {
            return null;
        }
        if (path.startsWith(ComfyUIConstants.JsonFields.INPUTS + "/")) {
            String inputName = path.substring((ComfyUIConstants.JsonFields.INPUTS + "/").length());
            return node.getInput(inputName);
        }
        return node.getInput(path);
    }

    /**
     * 获取节点属性（类型安全）
     */
    public <T> T getProperty(String nodeId, String path, Class<T> type) {
        Object value = getProperty(nodeId, path);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new ClassCastException("Property " + path + " of node " + nodeId + " is not of type " + type.getName());
    }

    // ==================== 便捷方法 ====================

    public Workflow setPrompt(String nodeId, String prompt) {
        return setProperty(nodeId, ComfyUIConstants.JsonFields.TEXT, prompt); // CLIPTextEncode uses 'text' input
    }

    public Workflow setNegativePrompt(String nodeId, String prompt) {
        return setProperty(nodeId, ComfyUIConstants.JsonFields.TEXT, prompt);
    }

    public Workflow setCheckpoint(String nodeId, String checkpointName) {
        return setProperty(nodeId, ComfyUIConstants.InputFields.CKPT_NAME, checkpointName);
    }

    /**
     * 设置节点的seed值，自动检测节点使用的seed字段名称
     * 支持：seed, noise_seed, ctrl_seed
     */
    public Workflow setSeed(String nodeId, long seed) {
        WorkflowNode node = getNode(nodeId);
        if (node == null) {
            throw new WorkflowException("Node not found: " + nodeId);
        }
        
        // 自动检测该节点使用的 seed 字段
        for (String fieldName : SEED_FIELD_NAMES) {
            if (node.getInputs().has(fieldName)) {
                return setProperty(nodeId, fieldName, seed);
            }
        }
        
        // 默认设置 seed 字段
        return setProperty(nodeId, ComfyUIConstants.InputFields.SEED, seed);
    }

    /**
     * 随机化所有节点的seed值
     */
    public Workflow randomizeSeed() {
        long newSeed = Math.abs(new Random().nextLong());
        
        // 查找所有具有 seed 相关字段的节点并设置
        nodes.values().forEach(node -> {
            for (String fieldName : SEED_FIELD_NAMES) {
                if (node.getInputs().has(fieldName)) {
                    setProperty(node.getId(), fieldName, newSeed);
                }
            }
        });
        return this;
    }

    /**
     * 设置文件输入（根据节点类型自动选择正确的输入字段）
     * 支持 LoadImage、LoadAudio、LoadVideo 等节点
     */
    public Workflow setFileInput(String nodeId, String filename) {
        WorkflowNode node = getNode(nodeId);
        if (node == null) {
            throw new WorkflowException("Node not found: " + nodeId);
        }
        
        String inputField = switch (node.getClassType()) {
            case ComfyUIConstants.NodeTypes.LOAD_IMAGE -> ComfyUIConstants.InputFields.IMAGE;
            case ComfyUIConstants.NodeTypes.LOAD_AUDIO -> ComfyUIConstants.InputFields.AUDIO;
            case ComfyUIConstants.NodeTypes.LOAD_VIDEO -> ComfyUIConstants.InputFields.VIDEO;
            default -> throw new WorkflowException("Unsupported node type for file input: " + node.getClassType());
        };
        
        return setProperty(nodeId, inputField, filename);
    }

    // ==================== 转换方法 ====================

    public String toApiJson() {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, WorkflowNode> entry : nodes.entrySet()) {
            ObjectNode nodeData = JsonNodeFactory.instance.objectNode();
            nodeData.put(ComfyUIConstants.JsonFields.CLASS_TYPE, entry.getValue().getClassType());
            nodeData.set(ComfyUIConstants.JsonFields.INPUTS, entry.getValue().getInputs());
            JsonNode meta = entry.getValue().getMeta();
            if (meta != null && !meta.isMissingNode() && !meta.isEmpty()) {
                nodeData.set(ComfyUIConstants.JsonFields.META, meta);
            }
            output.set(entry.getKey(), nodeData);
        }
        return JacksonUtils.writeValueAsString(output);
    }

    public JsonNode toJsonNode() {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, WorkflowNode> entry : nodes.entrySet()) {
            ObjectNode nodeData = JsonNodeFactory.instance.objectNode();
            nodeData.put(ComfyUIConstants.JsonFields.CLASS_TYPE, entry.getValue().getClassType());
            nodeData.set(ComfyUIConstants.JsonFields.INPUTS, entry.getValue().getInputs());
            JsonNode meta = entry.getValue().getMeta();
            if (meta != null && !meta.isMissingNode() && !meta.isEmpty()) {
                nodeData.set(ComfyUIConstants.JsonFields.META, meta);
            }
            output.set(entry.getKey(), nodeData);
        }
        return output;
    }

    public Workflow copy() {
        return fromApiJson(toApiJson());
    }
}
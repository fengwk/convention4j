package fun.fengwk.convention4j.comfyui.input;

import fun.fengwk.convention4j.comfyui.ComfyUIConstants;

import java.util.Set;

/**
 * 输入文件类型
 *
 * @author fengwk
 */
public enum InputType {
    IMAGE(
        Set.of(ComfyUIConstants.NodeTypes.LOAD_IMAGE),
        ComfyUIConstants.InputFields.IMAGE
    ),
    AUDIO(
        Set.of(ComfyUIConstants.NodeTypes.LOAD_AUDIO),
        ComfyUIConstants.InputFields.AUDIO
    ),
    VIDEO(
        Set.of(ComfyUIConstants.NodeTypes.LOAD_VIDEO),
        ComfyUIConstants.InputFields.VIDEO
    );
    
    private final Set<String> nodeTypes;
    private final String inputField;
    
    InputType(Set<String> nodeTypes, String inputField) {
        this.nodeTypes = nodeTypes;
        this.inputField = inputField;
    }
    
    /**
     * 获取支持的节点类型集合
     */
    public Set<String> getNodeTypes() {
        return nodeTypes;
    }
    
    /**
     * 获取输入字段名称
     */
    public String getInputField() {
        return inputField;
    }
    
    /**
     * 检查节点类型是否匹配
     */
    public boolean matchesNodeType(String nodeType) {
        return nodeTypes.contains(nodeType);
    }
}
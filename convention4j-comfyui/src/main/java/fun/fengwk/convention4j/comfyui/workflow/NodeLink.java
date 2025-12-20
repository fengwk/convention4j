package fun.fengwk.convention4j.comfyui.workflow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 节点链接
 *
 * @author fengwk
 */
@Getter
@ToString
@RequiredArgsConstructor
public class NodeLink {
    /**
     * 目标节点ID
     */
    private final String targetNodeId;

    /**
     * 目标节点输入名称
     */
    private final String targetInputName;

    /**
     * 源节点ID
     */
    private final String sourceNodeId;

    /**
     * 源节点输出索引
     */
    private final int sourceOutputIndex;
}
package fun.fengwk.convention4j.comfyui;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ComfyUI任务详情
 *
 * @author fengwk
 */
@Getter
@RequiredArgsConstructor
public class ComfyUIJob {

    private final String id;

    private final String status;

    private final Double priority;

    private final Long createTime;

    private final Long updateTime;

    private final String workflowId;

    private final Long executionStartTime;

    private final Long executionEndTime;

    private final Integer outputsCount;

    private final JsonNode outputs;

    private final JsonNode executionError;

    private final JsonNode executionStatus;

    private final JsonNode workflow;

    private final JsonNode previewOutput;
}

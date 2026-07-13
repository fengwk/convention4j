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

    private final long createTime;

    private final long updateTime;

    private final Integer outputsCount;

    private final JsonNode outputs;

    private final JsonNode executionError;

    private final JsonNode executionStatus;

    private final JsonNode workflow;

    private final JsonNode previewOutput;
}

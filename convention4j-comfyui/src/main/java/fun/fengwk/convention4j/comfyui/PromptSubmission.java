package fun.fengwk.convention4j.comfyui;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 工作流提交结果
 *
 * @author fengwk
 */
@Getter
@RequiredArgsConstructor
public class PromptSubmission {

    private final String promptId;

    private final double number;

    private final JsonNode nodeErrors;
}

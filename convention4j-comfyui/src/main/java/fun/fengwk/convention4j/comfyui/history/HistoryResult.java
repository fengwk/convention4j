package fun.fengwk.convention4j.comfyui.history;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 历史记录结果
 *
 * @author fengwk
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResult {

    /**
     * outputs: { "nodeId": { "images": [...], "text": [...], "value": [...] } }
     */
    private Map<String, JsonNode> outputs;
    
}
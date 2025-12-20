package fun.fengwk.convention4j.comfyui.output;

import com.fasterxml.jackson.databind.JsonNode;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 节点输出
 *
 * @author fengwk
 */
@Getter
@RequiredArgsConstructor
public class NodeOutput {
    /**
     * 节点ID
     */
    private final String nodeId;

    /**
     * 输出文件列表
     */
    private final List<OutputFile> outputs;

    /**
     * 原始输出数据
     */
    private final JsonNode data;

    /**
     * 判断是否有图片输出
     *
     * @return 是否有图片
     */
    public boolean hasImages() {
        return data != null && data.has(ComfyUIConstants.JsonFields.IMAGES) && data.get(ComfyUIConstants.JsonFields.IMAGES).isArray() && !data.get(ComfyUIConstants.JsonFields.IMAGES).isEmpty();
    }

    /**
     * 判断是否有视频输出
     *
     * @return 是否有视频
     */
    public boolean hasVideos() {
        return data != null && data.has(ComfyUIConstants.JsonFields.VIDEO) && data.get(ComfyUIConstants.JsonFields.VIDEO).isArray() && !data.get(ComfyUIConstants.JsonFields.VIDEO).isEmpty();
    }

    /**
     * 判断是否有音频输出
     *
     * @return 是否有音频
     */
    public boolean hasAudios() {
        return data != null && data.has(ComfyUIConstants.JsonFields.AUDIO) && data.get(ComfyUIConstants.JsonFields.AUDIO).isArray() && !data.get(ComfyUIConstants.JsonFields.AUDIO).isEmpty();
    }

    /**
     * 判断是否有文本输出
     *
     * @return 是否有文本
     */
    public boolean hasText() {
        return data != null && data.has(ComfyUIConstants.JsonFields.TEXT) && data.get(ComfyUIConstants.JsonFields.TEXT).isArray() && !data.get(ComfyUIConstants.JsonFields.TEXT).isEmpty();
    }

    /**
     * 判断是否有数值输出
     *
     * @return 是否有数值
     */
    public boolean hasValue() {
        return data != null && data.has(ComfyUIConstants.JsonFields.VALUE_OUTPUT) && data.get(ComfyUIConstants.JsonFields.VALUE_OUTPUT).isArray() && !data.get(ComfyUIConstants.JsonFields.VALUE_OUTPUT).isEmpty();
    }

    public List<OutputFile> getImages() {
        return outputs.stream().filter(OutputFile::isImage).toList();
    }

    public List<OutputFile> getVideos() {
        return outputs.stream().filter(OutputFile::isVideo).toList();
    }

    public List<OutputFile> getAudios() {
        return outputs.stream().filter(OutputFile::isAudio).toList();
    }

    public List<OutputFile> getFiles() {
        return Collections.unmodifiableList(outputs);
    }

    /**
     * 获取文本类型输出
     *
     * @return 文本输出列表
     */
    public List<String> getText() {
        if (data != null && data.has(ComfyUIConstants.JsonFields.TEXT)) {
            JsonNode textNode = data.get(ComfyUIConstants.JsonFields.TEXT);
            List<String> result = new ArrayList<>();
            if (textNode.isArray()) {
                for (JsonNode item : textNode) {
                    result.add(item.asText());
                }
            }
            return result;
        }
        return List.of();
    }

    /**
     * 获取数值类型输出
     *
     * @return 数值输出列表
     */
    public List<Number> getValue() {
        if (data != null && data.has(ComfyUIConstants.JsonFields.VALUE_OUTPUT)) {
            JsonNode valueNode = data.get(ComfyUIConstants.JsonFields.VALUE_OUTPUT);
            List<Number> result = new ArrayList<>();
            if (valueNode.isArray()) {
                for (JsonNode item : valueNode) {
                    if (item.isIntegralNumber()) {
                        result.add(item.asLong());
                    } else if (item.isFloatingPointNumber()) {
                        result.add(item.asDouble());
                    }
                }
            }
            return result;
        }
        return List.of();
    }
}


package fun.fengwk.convention4j.comfyui.output;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 输出文件元数据
 * 仅包含文件的元信息，不包含实际数据
 * 用户需要通过 client.getFile(outputFile) 来获取文件数据
 *
 * @author fengwk
 */
@Getter
@RequiredArgsConstructor
public class OutputFile {
    /**
     * 文件名
     */
    private final String filename;

    /**
     * 子文件夹
     */
    private final String subfolder;

    /**
     * 文件夹类型: temp, output
     */
    private final String folderType;

    /**
     * 输出类型
     */
    private final OutputType outputType;

    /**
     * MIME类型
     */
    private final String mimeType;

    public boolean isImage() {
        return outputType == OutputType.IMAGE;
    }

    public boolean isVideo() {
        return outputType == OutputType.VIDEO;
    }

    public boolean isAudio() {
        return outputType == OutputType.AUDIO;
    }
}
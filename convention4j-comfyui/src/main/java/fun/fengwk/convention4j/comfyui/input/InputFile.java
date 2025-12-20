package fun.fengwk.convention4j.comfyui.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 输入文件
 *
 * @author fengwk
 */
@Getter
@RequiredArgsConstructor
public class InputFile {
    /**
     * 文件名
     */
    private final String filename;

    /**
     * 文件数据
     */
    private final byte[] data;

    /**
     * MIME类型
     */
    private final String mimeType;
}
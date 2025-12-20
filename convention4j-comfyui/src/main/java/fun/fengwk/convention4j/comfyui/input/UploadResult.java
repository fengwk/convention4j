package fun.fengwk.convention4j.comfyui.input;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 上传结果
 *
 * @author fengwk
 */
@Getter
@RequiredArgsConstructor
public class UploadResult {
    /**
     * 文件名
     */
    private final String name;

    /**
     * 子文件夹
     */
    private final String subfolder;
}
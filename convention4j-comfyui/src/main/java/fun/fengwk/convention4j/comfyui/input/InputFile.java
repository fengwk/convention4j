package fun.fengwk.convention4j.comfyui.input;

import lombok.Getter;

/**
 * 输入文件
 *
 * @author fengwk
 */
@Getter
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
    
    /**
     * 文件类型
     */
    private final InputType type;
    
    /**
     * 完整构造方法
     */
    public InputFile(String filename, byte[] data, String mimeType, InputType type) {
        this.filename = filename;
        this.data = data;
        this.mimeType = mimeType;
        this.type = type;
    }
    
    /**
     * 便捷构造方法（自动推断类型）
     */
    public InputFile(String filename, byte[] data, String mimeType) {
        this.filename = filename;
        this.data = data;
        this.mimeType = mimeType;
        this.type = inferType(mimeType);
    }
    
    /**
     * 根据 MIME 类型推断文件类型
     */
    private static InputType inferType(String mimeType) {
        if (mimeType == null) {
            return InputType.IMAGE; // 默认
        }
        if (mimeType.startsWith("image/")) {
            return InputType.IMAGE;
        }
        if (mimeType.startsWith("audio/")) {
            return InputType.AUDIO;
        }
        if (mimeType.startsWith("video/")) {
            return InputType.VIDEO;
        }
        return InputType.IMAGE;
    }
    
    /**
     * 创建图像输入文件
     */
    public static InputFile image(String filename, byte[] data, String mimeType) {
        return new InputFile(filename, data, mimeType, InputType.IMAGE);
    }
    
    /**
     * 创建音频输入文件
     */
    public static InputFile audio(String filename, byte[] data, String mimeType) {
        return new InputFile(filename, data, mimeType, InputType.AUDIO);
    }
    
    /**
     * 创建视频输入文件
     */
    public static InputFile video(String filename, byte[] data, String mimeType) {
        return new InputFile(filename, data, mimeType, InputType.VIDEO);
    }
}
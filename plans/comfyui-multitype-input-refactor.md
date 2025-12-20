# ComfyUI SDK 多类型输入重构方案

## 1. 背景

当前 ComfyUI SDK 主要针对图像输入场景设计，但 ComfyUI 实际支持多种类型的输入文件：
- **Image**: LoadImage 节点 - 输入字段 `image`
- **Audio**: LoadAudio 节点 - 输入字段 `audio`  
- **Video**: LoadVideo 节点 - 输入字段 `video`

需要重构 SDK 以支持这些多媒体类型的输入。

## 2. 需要修改的文件

### 2.1 ComfyUIClient.java (接口变更)

**变更内容**：
- 将 `uploadImage` 方法重命名为 `uploadFile`，使其语义更通用

```java
// Before
Mono<UploadResult> uploadImage(String filename, byte[] data, String mimeType);

// After
Mono<UploadResult> uploadFile(String filename, byte[] data, String mimeType);
```

### 2.2 DefaultComfyUIClient.java (实现变更)

**变更内容**：
1. 将 `uploadImage` 方法重命名为 `uploadFile`
2. 修改 multipart body 中 form field 名称的处理（ComfyUI 上传 API 使用 "image" 作为 field name，但实际上接受任何文件类型）

```java
// 方法签名变更
@Override
public Mono<UploadResult> uploadFile(String filename, byte[] data, String mimeType) {
    // 实现保持不变，ComfyUI 的 /upload/image 端点实际上接受任何文件
}
```

### 2.3 ComfyUIConstants.java (常量扩展)

**新增节点类型**：
```java
public static final class NodeTypes {
    public static final String LOAD_IMAGE = "LoadImage";
    public static final String LOAD_AUDIO = "LoadAudio";
    public static final String LOAD_VIDEO = "LoadVideo";
}
```

**新增输入字段**：
```java
public static final class InputFields {
    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";
    // 现有字段保持不变
}
```

### 2.4 Workflow.java (工作流扩展)

**新增方法**：

```java
// Audio 输入方法
public Workflow setAudioInput(String nodeId, String audioName) {
    return setProperty(nodeId, ComfyUIConstants.InputFields.AUDIO, audioName);
}

public Workflow setAudioInputs(List<String> audioNames) {
    List<WorkflowNode> loadNodes = getNodesByType(ComfyUIConstants.NodeTypes.LOAD_AUDIO);
    for (int i = 0; i < Math.min(audioNames.size(), loadNodes.size()); i++) {
        setAudioInput(loadNodes.get(i).getId(), audioNames.get(i));
    }
    return this;
}

public Workflow setAudioInputs(List<String> audioNames, List<String> nodeIds) {
    for (int i = 0; i < Math.min(audioNames.size(), nodeIds.size()); i++) {
        setAudioInput(nodeIds.get(i), audioNames.get(i));
    }
    return this;
}

// Video 输入方法
public Workflow setVideoInput(String nodeId, String videoName) {
    return setProperty(nodeId, ComfyUIConstants.InputFields.VIDEO, videoName);
}

public Workflow setVideoInputs(List<String> videoNames) {
    List<WorkflowNode> loadNodes = getNodesByType(ComfyUIConstants.NodeTypes.LOAD_VIDEO);
    for (int i = 0; i < Math.min(videoNames.size(), loadNodes.size()); i++) {
        setVideoInput(loadNodes.get(i).getId(), videoNames.get(i));
    }
    return this;
}

public Workflow setVideoInputs(List<String> videoNames, List<String> nodeIds) {
    for (int i = 0; i < Math.min(videoNames.size(), nodeIds.size()); i++) {
        setVideoInput(nodeIds.get(i), videoNames.get(i));
    }
    return this;
}

// 通用的文件输入方法（根据节点类型自动选择字段）
public Workflow setFileInput(String nodeId, String filename) {
    WorkflowNode node = getNode(nodeId);
    if (node == null) {
        throw new WorkflowException("Node not found: " + nodeId);
    }
    
    String inputField = switch (node.getClassType()) {
        case ComfyUIConstants.NodeTypes.LOAD_IMAGE -> ComfyUIConstants.InputFields.IMAGE;
        case ComfyUIConstants.NodeTypes.LOAD_AUDIO -> ComfyUIConstants.InputFields.AUDIO;
        case ComfyUIConstants.NodeTypes.LOAD_VIDEO -> ComfyUIConstants.InputFields.VIDEO;
        default -> throw new WorkflowException("Unsupported node type for file input: " + node.getClassType());
    };
    
    return setProperty(nodeId, inputField, filename);
}
```

### 2.5 InputFile.java (增强)

**新增类型枚举和工厂方法**：

```java
public enum InputType {
    IMAGE, AUDIO, VIDEO
}

@Getter
@RequiredArgsConstructor
public class InputFile {
    private final String filename;
    private final byte[] data;
    private final String mimeType;
    private final InputType type;
    
    // 便捷构造方法（自动推断类型）
    public InputFile(String filename, byte[] data, String mimeType) {
        this.filename = filename;
        this.data = data;
        this.mimeType = mimeType;
        this.type = inferType(mimeType);
    }
    
    private static InputType inferType(String mimeType) {
        if (mimeType == null) return InputType.IMAGE; // 默认
        if (mimeType.startsWith("image/")) return InputType.IMAGE;
        if (mimeType.startsWith("audio/")) return InputType.AUDIO;
        if (mimeType.startsWith("video/")) return InputType.VIDEO;
        return InputType.IMAGE;
    }
    
    // 静态工厂方法
    public static InputFile image(String filename, byte[] data, String mimeType) {
        return new InputFile(filename, data, mimeType, InputType.IMAGE);
    }
    
    public static InputFile audio(String filename, byte[] data, String mimeType) {
        return new InputFile(filename, data, mimeType, InputType.AUDIO);
    }
    
    public static InputFile video(String filename, byte[] data, String mimeType) {
        return new InputFile(filename, data, mimeType, InputType.VIDEO);
    }
}
```

### 2.6 ExecutionOptions.java (重构)

**变更内容**：将 `imageNodeIds` 替换为更通用的节点映射机制

```java
@Builder
@Getter
public class ExecutionOptions {
    @Builder.Default
    private final boolean randomizeSeed = false;
    
    /**
     * 输入文件列表
     */
    private final List<InputFile> inputFiles;
    
    /**
     * 图像节点ID映射（指定图像输入顺序）
     */
    private final List<String> imageNodeIds;
    
    /**
     * 音频节点ID映射（指定音频输入顺序）
     */
    private final List<String> audioNodeIds;
    
    /**
     * 视频节点ID映射（指定视频输入顺序）
     */
    private final List<String> videoNodeIds;
    
    /**
     * 执行监听器
     */
    private final ExecutionListener listener;
    
    /**
     * 执行超时时间
     */
    private final Duration timeout;
}
```

### 2.7 DefaultComfyUIClient.java (执行逻辑更新)

**更新 executeWithEvents 方法中的输入处理**：

```java
@Override
public Flux<ExecutionEvent> executeWithEvents(Workflow workflow, ExecutionOptions options) {
    Flux<ExecutionEvent> eventFlux = Mono.fromCallable(() -> {
                // 1. 处理输入文件上传
                if (options.getInputFiles() != null && !options.getInputFiles().isEmpty()) {
                    for (InputFile inputFile : options.getInputFiles()) {
                        uploadFile(inputFile.getFilename(), inputFile.getData(), inputFile.getMimeType()).block();
                    }
                }

                // 2. 随机种子
                Workflow finalWorkflow = workflow.copy();
                if (options.isRandomizeSeed()) {
                    finalWorkflow.randomizeSeed();
                }

                // 3. 设置图像输入节点
                if (options.getImageNodeIds() != null && options.getInputFiles() != null) {
                    List<String> imageNames = options.getInputFiles().stream()
                            .filter(f -> f.getType() == InputFile.InputType.IMAGE)
                            .map(InputFile::getFilename)
                            .collect(Collectors.toList());
                    finalWorkflow.setImageInputs(imageNames, options.getImageNodeIds());
                }
                
                // 4. 设置音频输入节点
                if (options.getAudioNodeIds() != null && options.getInputFiles() != null) {
                    List<String> audioNames = options.getInputFiles().stream()
                            .filter(f -> f.getType() == InputFile.InputType.AUDIO)
                            .map(InputFile::getFilename)
                            .collect(Collectors.toList());
                    finalWorkflow.setAudioInputs(audioNames, options.getAudioNodeIds());
                }
                
                // 5. 设置视频输入节点
                if (options.getVideoNodeIds() != null && options.getInputFiles() != null) {
                    List<String> videoNames = options.getInputFiles().stream()
                            .filter(f -> f.getType() == InputFile.InputType.VIDEO)
                            .map(InputFile::getFilename)
                            .collect(Collectors.toList());
                    finalWorkflow.setVideoInputs(videoNames, options.getVideoNodeIds());
                }

                // 6. 提交任务
                return queuePrompt(finalWorkflow);
            })
            // ... 后续处理不变
}
```

## 3. 使用示例

### 3.1 图像输入（保持向后兼容）

```java
// 原有用法仍然有效
workflow.setImageInput("4", "my-image.png");

// 或使用列表
workflow.setImageInputs(List.of("img1.png", "img2.png"));
```

### 3.2 音频输入

```java
// 单个音频
workflow.setAudioInput("47", "audio.mp3");

// 多个音频
workflow.setAudioInputs(List.of("audio1.mp3", "audio2.wav"));
```

### 3.3 视频输入

```java
// 单个视频
workflow.setVideoInput("5", "video.mp4");
```

### 3.4 通用文件输入（自动检测类型）

```java
// 自动根据节点类型设置正确的输入字段
workflow.setFileInput("47", "input.mp3"); // LoadAudio 节点
workflow.setFileInput("4", "input.png");  // LoadImage 节点
```

### 3.5 执行选项

```java
ExecutionOptions options = ExecutionOptions.builder()
    .inputFiles(List.of(
        InputFile.image("photo.png", imageBytes, "image/png"),
        InputFile.audio("music.mp3", audioBytes, "audio/mpeg")
    ))
    .imageNodeIds(List.of("4"))
    .audioNodeIds(List.of("47"))
    .randomizeSeed(true)
    .build();

client.execute(workflow, options);
```

## 4. 实施顺序

1. 修改 `ComfyUIClient` 接口，添加 `uploadFile` 方法
2. 修改 `DefaultComfyUIClient` 实现
3. 扩展 `ComfyUIConstants` 常量
4. 扩展 `InputFile` 类型枚举
5. 扩展 `Workflow` 类便捷方法
6. 扩展 `ExecutionOptions` 节点映射
7. 更新 `DefaultComfyUIClient` 执行逻辑
8. 添加测试用例
9. 更新 README 文档
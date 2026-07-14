package fun.fengwk.convention4j.comfyui;

import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionOptions;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.history.HistoryResult;
import fun.fengwk.convention4j.comfyui.input.InputFile;
import fun.fengwk.convention4j.comfyui.input.InputType;
import fun.fengwk.convention4j.comfyui.input.UploadResult;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ComfyUI客户端便捷方法测试
 *
 * @author fengwk
 */
public class ComfyUIClientConvenienceTest {

    @Test
    public void testUploadAndSetFilesSequentiallyUsesReturnedPaths() {
        ControlledUploadClient client = new ControlledUploadClient();
        Workflow workflow = Workflow.fromApiJson("""
                {
                  "1": {"class_type": "LoadImage", "inputs": {}},
                  "2": {"class_type": "LoadImage", "inputs": {}}
                }
                """);
        byte[] firstData = "first-original".getBytes(StandardCharsets.UTF_8);
        List<InputFile> files = new ArrayList<>(List.of(
                InputFile.image("first.png", firstData, "image/png"),
                InputFile.image("second.png", new byte[]{2}, "image/png")));
        List<String> nodeIds = new ArrayList<>(List.of("1", "2"));

        Mono<Workflow> resultMono = client.uploadAndSetFiles(workflow, files, nodeIds);
        // 组装后修改调用方数据，证明上传顺序、字节内容和绑定使用防御性快照。
        Arrays.fill(firstData, (byte) 'x');
        files.clear();
        nodeIds.clear();
        var resultFuture = resultMono.toFuture();

        // concatMap只会先订阅第一个上传，防止并发修改同一个Workflow/ObjectNode。
        Assertions.assertEquals(List.of("first.png"), client.getUploadCalls());
        Assertions.assertEquals("first-original", client.getUploadBodies().get(0));
        client.completeFirst(new UploadResult("first (1).png", "nested"));
        Assertions.assertEquals(List.of("first.png", "second.png"), client.getUploadCalls());
        client.completeSecond(new UploadResult("second (1).png", ""));

        Workflow result = resultFuture.join();
        Assertions.assertEquals("nested/first (1).png", result.getNode("1").getInput("image").asText());
        Assertions.assertEquals("second (1).png", result.getNode("2").getInput("image").asText());
    }

    @Test
    public void testStatelessDefaultsPreserveCustomClientCompatibility() {
        ControlledUploadClient client = new ControlledUploadClient();
        // 旧的自定义实现无需新增方法；调用新能力时通过响应式错误明确报告不支持。
        Assertions.assertThrows(UnsupportedOperationException.class, () -> client.submit(null).block());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> client.getJob("job").block());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> client.cancelJob("job").block());
    }

    @Test
    public void testInputFileInfersMixedCaseTrimmedMediaMimeTypes() {
        // MIME 推断与执行绑定使用同一InputType，必须接受客户端常见的大小写和空白差异。
        Assertions.assertEquals(InputType.AUDIO,
                new InputFile("input.wav", new byte[]{1}, " Audio/WAV ").getType());
        Assertions.assertEquals(InputType.VIDEO,
                new InputFile("input.mp4", new byte[]{1}, "VIDEO/MP4").getType());
    }

    @Test
    public void testInvalidFileBindingsFailBeforeAnyUpload() {
        ControlledUploadClient client = new ControlledUploadClient();
        Workflow workflow = Workflow.fromApiJson("""
                {
                  "1": {"class_type": "LoadImage", "inputs": {}},
                  "2": {"class_type": "TestNode", "inputs": {}}
                }
                """);

        // 逐项覆盖空文件、无效节点、节点类型不支持、类型不匹配和无效上传参数，均不得发起首个上传。
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.uploadAndSetFile(workflow, " ", "input.png", new byte[]{1}, "image/png").block());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.uploadAndSetFile(workflow, "missing", "input.png", new byte[]{1}, "image/png").block());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.uploadAndSetFile(workflow, "2", "input.png", new byte[]{1}, "image/png").block());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.uploadAndSetFile(workflow, "1", "input.png", null, "image/png").block());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.uploadAndSetFiles(workflow, Collections.singletonList(null), List.of("1")).block());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.uploadAndSetFiles(workflow,
                        List.of(InputFile.audio("input.wav", new byte[]{1}, "audio/wav")), List.of("1")).block());

        Assertions.assertTrue(client.getUploadCalls().isEmpty());
    }

    private static class ControlledUploadClient implements ComfyUIClient {

        private final List<String> uploadCalls = new ArrayList<>();
        private final List<String> uploadBodies = new ArrayList<>();
        private final Sinks.One<UploadResult> first = Sinks.one();
        private final Sinks.One<UploadResult> second = Sinks.one();

        public List<String> getUploadCalls() {
            return List.copyOf(uploadCalls);
        }

        public List<String> getUploadBodies() {
            return List.copyOf(uploadBodies);
        }

        public void completeFirst(UploadResult result) {
            first.tryEmitValue(result);
        }

        public void completeSecond(UploadResult result) {
            second.tryEmitValue(result);
        }

        @Override
        public Mono<UploadResult> uploadFile(String filename, byte[] data, String mimeType) {
            uploadCalls.add(filename);
            uploadBodies.add(new String(data, StandardCharsets.UTF_8));
            return uploadCalls.size() == 1 ? first.asMono() : second.asMono();
        }

        @Override
        public Mono<ExecutionResult> execute(Workflow workflow) {
            return Mono.error(new UnsupportedOperationException());
        }

        @Override
        public Mono<ExecutionResult> execute(Workflow workflow, ExecutionOptions options) {
            return Mono.error(new UnsupportedOperationException());
        }

        @Override
        public Flux<ExecutionEvent> executeWithEvents(Workflow workflow) {
            return Flux.error(new UnsupportedOperationException());
        }

        @Override
        public Flux<ExecutionEvent> executeWithEvents(Workflow workflow, ExecutionOptions options) {
            return Flux.error(new UnsupportedOperationException());
        }

        @Override
        public Mono<byte[]> getFile(String filename, String subfolder, String type) {
            return Mono.error(new UnsupportedOperationException());
        }

        @Override
        public Mono<HistoryResult> getHistory(String promptId) {
            return Mono.error(new UnsupportedOperationException());
        }

        @Override
        public void close() {
        }
    }
}

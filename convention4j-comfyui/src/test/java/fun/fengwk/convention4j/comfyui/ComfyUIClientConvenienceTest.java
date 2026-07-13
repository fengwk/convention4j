package fun.fengwk.convention4j.comfyui;

import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionOptions;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.history.HistoryResult;
import fun.fengwk.convention4j.comfyui.input.InputFile;
import fun.fengwk.convention4j.comfyui.input.UploadResult;
import fun.fengwk.convention4j.comfyui.workflow.Workflow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
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
        List<InputFile> files = List.of(
                InputFile.image("first.png", new byte[]{1}, "image/png"),
                InputFile.image("second.png", new byte[]{2}, "image/png"));

        Mono<Workflow> resultMono = client.uploadAndSetFiles(workflow, files, List.of("1", "2"));
        var resultFuture = resultMono.toFuture();

        // concatMap只会先订阅第一个上传，防止并发修改同一个Workflow/ObjectNode。
        Assertions.assertEquals(List.of("first.png"), client.getUploadCalls());
        client.completeFirst(new UploadResult("first (1).png", "nested"));
        Assertions.assertEquals(List.of("first.png", "second.png"), client.getUploadCalls());
        client.completeSecond(new UploadResult("second (1).png", ""));

        Workflow result = resultFuture.join();
        Assertions.assertEquals("nested/first (1).png", result.getNode("1").getInput("image").asText());
        Assertions.assertEquals("second (1).png", result.getNode("2").getInput("image").asText());
    }

    private static class ControlledUploadClient implements ComfyUIClient {

        private final List<String> uploadCalls = new ArrayList<>();
        private final Sinks.One<UploadResult> first = Sinks.one();
        private final Sinks.One<UploadResult> second = Sinks.one();

        public List<String> getUploadCalls() {
            return List.copyOf(uploadCalls);
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
            return uploadCalls.size() == 1 ? first.asMono() : second.asMono();
        }

        @Override
        public Mono<PromptSubmission> submit(Workflow workflow) {
            return Mono.error(new UnsupportedOperationException());
        }

        @Override
        public Mono<ComfyUIJob> getJob(String jobId) {
            return Mono.error(new UnsupportedOperationException());
        }

        @Override
        public Mono<Boolean> cancelJob(String jobId) {
            return Mono.error(new UnsupportedOperationException());
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

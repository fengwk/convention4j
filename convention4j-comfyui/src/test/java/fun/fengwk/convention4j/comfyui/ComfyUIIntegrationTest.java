//package fun.fengwk.convention4j.comfyui;
//
//import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
//import fun.fengwk.convention4j.comfyui.output.OutputFile;
//import fun.fengwk.convention4j.comfyui.workflow.Workflow;
//import fun.fengwk.convention4j.common.http.client.HttpClientFactory;
//import fun.fengwk.convention4j.common.io.IoUtils;
//import fun.fengwk.convention4j.common.json.JsonUtils;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Assumptions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.Duration;
//
///**
// * 集成测试
// *
// * @author fengwk
// */
//public class ComfyUIIntegrationTest {
//
//    private static final String BASE_URL = "http://127.0.0.1:8188";
//
//    @BeforeAll
//    public static void checkComfyUI() {
//        try {
//            HttpClient client = HttpClientFactory.getDefaultHttpClient();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(BASE_URL))
//                    .GET()
//                    .timeout(Duration.ofSeconds(2))
//                    .build();
//            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
//            Assumptions.assumeTrue(response.statusCode() == 200, "ComfyUI is not running");
//        } catch (Exception e) {
//            Assumptions.assumeTrue(false, "ComfyUI is not reachable: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testExecuteWorkflow() throws IOException {
//        String workflowJson = Files.readString(Paths.get("src/test/resources/workflow_audio.json"));
//        Workflow workflow = Workflow.fromApiJson(workflowJson);
//
//        ComfyUIClientFactory factory = new ComfyUIClientFactory();
//        try (ComfyUIClient client = factory.create(BASE_URL)) {
//            // 使用 block() 将异步调用转换为同步
//            ExecutionResult result = client.execute(workflow).block();
//
//            Assertions.assertNotNull(result);
//            Assertions.assertTrue(result.isSuccess());
//            System.out.println("Execution completed successfully. Prompt ID: " + result.getPromptId());
//
//            // 输出结果摘要
//            result.getNodeOutputs().forEach(((nodeId, nodeOutput) -> {
//                System.out.println("NodeID: " + nodeId + "=========================================");
//                System.out.println(JsonUtils.toJson(nodeOutput.getData()));
//                for (OutputFile outputFile : nodeOutput.getOutputs()) {
//                    if (outputFile.isImage() || outputFile.isAudio() || outputFile.isVideo()) {
//                        byte[] imageBytes = client.getFile(outputFile).block();
//                        if (imageBytes != null) {
//                            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
//                            String tmpDir = System.getProperty("java.io.tmpdir");
//                            String fileName = tmpDir + "/" + outputFile.getFilename();
//                            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
//                                IoUtils.copy(inputStream, outputStream);
//                                System.out.println("Image saved to: " + fileName);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }));
//        }
//    }
//
//    @Test
//    public void testUploadAndSetFile() throws IOException {
//        String workflowJson = Files.readString(Paths.get("src/test/resources/workflow_audio.json"));
//        Workflow workflow = Workflow.fromApiJson(workflowJson);
//
//        ComfyUIClientFactory factory = new ComfyUIClientFactory();
//        try (ComfyUIClient client = factory.create(BASE_URL)) {
//            // 读取音频文件
//            byte[] audioData = Files.readAllBytes(Paths.get("/home/fengwk/comfyui_data/input/Mahabbat.mp3"));
//
//            // 使用便捷方法：自动上传并设置到工作流
//            Workflow updatedWorkflow = client.uploadAndSetFile(
//                workflow,
//                "47",              // 节点ID
//                "test_mm.mp3",    // 新文件名
//                audioData,
//                "audio/mpeg"
//            ).block();
//
//            // 执行工作流
//            ExecutionResult result = client.execute(updatedWorkflow).block();
//
//            Assertions.assertNotNull(result);
//            Assertions.assertTrue(result.isSuccess());
//            System.out.println("Execution completed successfully. Prompt ID: " + result.getPromptId());
//
//            // 输出结果摘要
//            result.getNodeOutputs().forEach(((nodeId, nodeOutput) -> {
//                System.out.println("NodeID: " + nodeId + "=========================================");
//                System.out.println(JsonUtils.toJson(nodeOutput.getData()));
//                for (OutputFile outputFile : nodeOutput.getOutputs()) {
//                    if (outputFile.isImage() || outputFile.isAudio() || outputFile.isVideo()) {
//                        byte[] imageBytes = client.getFile(outputFile).block();
//                        if (imageBytes != null) {
//                            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
//                            String tmpDir = System.getProperty("java.io.tmpdir");
//                            String fileName = tmpDir + "/" + outputFile.getFilename();
//                            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
//                                IoUtils.copy(inputStream, outputStream);
//                                System.out.println("File saved to: " + fileName);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }));
//        }
//    }
//}
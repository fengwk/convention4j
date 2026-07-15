package fun.fengwk.convention4j.comfyui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.fengwk.convention4j.comfyui.execution.ExecutionEvent;
import fun.fengwk.convention4j.comfyui.execution.ExecutionResult;
import fun.fengwk.convention4j.comfyui.output.NodeOutput;
import fun.fengwk.convention4j.comfyui.output.OutputFile;
import fun.fengwk.convention4j.comfyui.output.OutputType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行结果模型的防御性所有权测试。
 *
 * @author fengwk
 */
public class ExecutionModelOwnershipTest {

    @Test
    public void testNodesCachedOwnsNodeIds() {
        List<String> constructorNodeIds = new ArrayList<>(List.of("1"));
        ExecutionEvent.NodesCached event = new ExecutionEvent.NodesCached(constructorNodeIds);

        constructorNodeIds.clear();
        constructorNodeIds.add("2");

        Assertions.assertEquals(List.of("1"), event.nodeIds());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> event.nodeIds().add("3"));
    }

    @Test
    public void testNodeOutputOwnsCollectionsAndJsonData() {
        OutputFile originalFile = outputFile("original.png");
        List<OutputFile> constructorOutputs = new ArrayList<>(List.of(originalFile));
        ObjectNode constructorData = JsonNodeFactory.instance.objectNode();
        constructorData.putArray(ComfyUIConstants.JsonFields.TEXT).add("original");
        constructorData.putObject("nested").put("value", "original");

        NodeOutput nodeOutput = new NodeOutput("1", constructorOutputs, constructorData);

        // 构造后的调用方修改不能改变节点输出持有的列表或深层 JSON。
        constructorOutputs.clear();
        constructorOutputs.add(outputFile("replacement.png"));
        constructorData.withArray(ComfyUIConstants.JsonFields.TEXT).add("mutated");
        ((ObjectNode) constructorData.path("nested")).put("value", "mutated");

        Assertions.assertEquals(List.of(originalFile), nodeOutput.getOutputs());
        Assertions.assertEquals(List.of(originalFile), nodeOutput.getFiles());
        Assertions.assertEquals(List.of("original"), nodeOutput.getText());
        Assertions.assertEquals("original", nodeOutput.getData().path("nested").path("value").asText());

        // getData 每次返回深拷贝，修改 getter 结果不能污染后续读取和便捷方法。
        JsonNode exposedData = nodeOutput.getData();
        ((ObjectNode) exposedData.path("nested")).put("value", "getter-mutated");
        ((ObjectNode) exposedData).remove(ComfyUIConstants.JsonFields.TEXT);
        Assertions.assertEquals("original", nodeOutput.getData().path("nested").path("value").asText());
        Assertions.assertEquals(List.of("original"), nodeOutput.getText());

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> nodeOutput.getOutputs().add(outputFile("forbidden.png")));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> nodeOutput.getFiles().clear());
    }

    @Test
    public void testExecutionResultOwnsOutputCollections() {
        OutputFile originalFile = outputFile("original.png");
        NodeOutput originalNodeOutput = new NodeOutput("1", List.of(originalFile),
                JsonNodeFactory.instance.objectNode());
        List<OutputFile> constructorOutputs = new ArrayList<>(List.of(originalFile));
        Map<String, NodeOutput> constructorNodeOutputs = new LinkedHashMap<>();
        constructorNodeOutputs.put("1", originalNodeOutput);

        ExecutionResult result = new ExecutionResult(
                "prompt-1", true, null, null, constructorOutputs, constructorNodeOutputs);

        // 构造后修改原始容器，结果仍保留创建时的列表和映射快照。
        constructorOutputs.clear();
        constructorOutputs.add(outputFile("replacement.png"));
        constructorNodeOutputs.clear();
        constructorNodeOutputs.put("2", new NodeOutput("2", List.of(), null));

        Assertions.assertEquals(List.of(originalFile), result.getOutputs());
        Assertions.assertEquals(Map.of("1", originalNodeOutput), result.getNodeOutputs());
        Assertions.assertSame(originalNodeOutput, result.getNodeOutput("1").orElseThrow());
        Assertions.assertTrue(result.getNodeOutput("2").isEmpty());

        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> result.getOutputs().add(outputFile("forbidden.png")));
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> result.getNodeOutputs().put("2", originalNodeOutput));
    }

    @Test
    public void testNullCollectionsBecomeImmutableEmptyCollections() {
        NodeOutput nodeOutput = new NodeOutput("1", null, null);
        Assertions.assertTrue(nodeOutput.getOutputs().isEmpty());
        Assertions.assertTrue(nodeOutput.getFiles().isEmpty());
        Assertions.assertNull(nodeOutput.getData());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> nodeOutput.getOutputs().add(outputFile("forbidden.png")));

        ExecutionResult result = new ExecutionResult("prompt-1", true, null, null, null, null);
        Assertions.assertTrue(result.getOutputs().isEmpty());
        Assertions.assertTrue(result.getNodeOutputs().isEmpty());
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> result.getOutputs().add(outputFile("forbidden.png")));
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> result.getNodeOutputs().put("1", nodeOutput));
    }

    private static OutputFile outputFile(String filename) {
        return new OutputFile(filename, "", "output", OutputType.IMAGE, "image/png");
    }
}

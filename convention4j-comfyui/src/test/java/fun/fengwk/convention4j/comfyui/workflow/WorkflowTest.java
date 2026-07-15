package fun.fengwk.convention4j.comfyui.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.fengwk.convention4j.comfyui.ComfyUIConstants;
import fun.fengwk.convention4j.comfyui.exception.WorkflowException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author fengwk
 */
public class WorkflowTest {

    @Test
    public void testFromApiJson() {
        String json = """
                {
                  "3": {
                    "inputs": {
                      "seed": 156680208700286,
                      "steps": 20,
                      "cfg": 8,
                      "sampler_name": "euler",
                      "scheduler": "normal",
                      "denoise": 1,
                      "model": [
                        "4",
                        0
                      ],
                      "positive": [
                        "6",
                        0
                      ],
                      "negative": [
                        "7",
                        0
                      ],
                      "latent_image": [
                        "5",
                        0
                      ]
                    },
                    "class_type": "KSampler",
                    "_meta": {
                      "title": "KSampler"
                    }
                  },
                  "4": {
                    "inputs": {
                      "ckpt_name": "v1-5-pruned-emaonly.ckpt"
                    },
                    "class_type": "CheckpointLoaderSimple",
                    "_meta": {
                      "title": "Load Checkpoint"
                    }
                  }
                }
                """;

        Workflow workflow = Workflow.fromApiJson(json);
        Assertions.assertNotNull(workflow);
        Assertions.assertEquals(2, workflow.getNodeIds().size());

        WorkflowNode kSampler = workflow.getNode("3");
        Assertions.assertNotNull(kSampler);
        Assertions.assertEquals("KSampler", kSampler.getClassType());
        Assertions.assertEquals(156680208700286L, kSampler.getInput("seed").asLong());
        Assertions.assertTrue(kSampler.isLinkedInput("model"));

        NodeLink link = kSampler.getLink("model").orElseThrow();
        Assertions.assertEquals("4", link.getSourceNodeId());
        Assertions.assertEquals(0, link.getSourceOutputIndex());
    }

    @Test
    public void testSetProperty() {
        String json = "{\"3\": {\"class_type\": \"KSampler\", \"inputs\": {\"seed\": 123}}}";
        Workflow workflow = Workflow.fromApiJson(json);
        
        workflow.setSeed("3", 456);
        WorkflowNode node = workflow.getNode("3");
        Assertions.assertEquals(456L, node.getInput("seed").asLong());
        
        workflow.setProperty("3", "inputs/steps", 20);
        Assertions.assertEquals(20, node.getInput("steps").asInt());
    }

    @Test
    public void testToJsonNodeDoesNotExposeWorkflowState() {
        Workflow workflow = Workflow.fromApiJson(
                "{\"3\": {\"class_type\": \"KSampler\", \"inputs\": {\"seed\": 123}}}");

        // 返回 JSON 必须是深拷贝，调用方修改它不能反向修改工作流。
        ObjectNode exportedInputs = (ObjectNode) workflow.toJsonNode().path("3").path("inputs");
        exportedInputs.put("seed", 456);

        Assertions.assertEquals(123L, workflow.getNode("3").getInput("seed").asLong());
        Assertions.assertEquals(123L, Workflow.fromApiJson(workflow.toApiJson())
                .getNode("3").getInput("seed").asLong());
    }

    @Test
    public void testRandomizeSeed() {
        String json = "{\"3\": {\"class_type\": \"KSampler\", \"inputs\": {\"seed\": 123}}}";
        Workflow workflow = Workflow.fromApiJson(json);

        workflow.randomizeSeed();
        long newSeed = workflow.getNode("3").getInput("seed").asLong();
        Assertions.assertNotEquals(123L, newSeed);
        Assertions.assertTrue(newSeed >= 0);
    }

    @Test
    public void testFromApiJsonRejectsNonObjectNodeValues() {
        // 每种 JSON 标量、数组和 null 都必须被拒绝，且消息能定位节点及对象约束。
        for (String bad : List.of(
                "{\"3\":\"hello\"}",
                "{\"3\":[1,2,3]}",
                "{\"3\":42}",
                "{\"3\":true}",
                "{\"3\":null}")) {
            assertWorkflowError(bad, "3", "object");
        }
    }

    @Test
    public void testFromApiJsonRejectsBlankNodeId() {
        ObjectNode blankIdRoot = JsonNodeFactory.instance.objectNode();
        ObjectNode blankIdInner = JsonNodeFactory.instance.objectNode();
        blankIdInner.put(ComfyUIConstants.JsonFields.CLASS_TYPE, "TestNode");
        blankIdInner.putObject(ComfyUIConstants.JsonFields.INPUTS);
        blankIdRoot.set("   ", blankIdInner);
        WorkflowException blankIdEx = Assertions.assertThrows(
                WorkflowException.class, () -> Workflow.fromApiJson(blankIdRoot.toString()));
        String message = blankIdEx.getMessage().toLowerCase();
        Assertions.assertTrue(message.contains("node id"), blankIdEx.getMessage());
        Assertions.assertTrue(message.contains("blank"), blankIdEx.getMessage());
    }

    @Test
    public void testFromApiJsonRejectsInvalidClassType() {
        // class_type 缺失、null、非文本或空白时，消息必须包含节点 ID 和字段名。
        for (String bad : List.of(
                "{\"3\":{\"inputs\":{}}}",
                "{\"3\":{\"class_type\":null,\"inputs\":{}}}",
                "{\"3\":{\"class_type\":42,\"inputs\":{}}}",
                "{\"3\":{\"class_type\":true,\"inputs\":{}}}",
                "{\"3\":{\"class_type\":[],\"inputs\":{}}}",
                "{\"3\":{\"class_type\":{\"x\":1},\"inputs\":{}}}",
                "{\"3\":{\"class_type\":\"   \",\"inputs\":{}}}")) {
            assertWorkflowError(bad, "3", "class_type");
        }
    }

    @Test
    public void testFromApiJsonRejectsInvalidInputs() {
        // inputs 缺失、null 或任意非对象类型时，消息必须包含节点 ID 和字段名。
        for (String bad : List.of(
                "{\"3\":{\"class_type\":\"X\"}}",
                "{\"3\":{\"class_type\":\"X\",\"inputs\":null}}",
                "{\"3\":{\"class_type\":\"X\",\"inputs\":[]}}",
                "{\"3\":{\"class_type\":\"X\",\"inputs\":\"abc\"}}",
                "{\"3\":{\"class_type\":\"X\",\"inputs\":42}}",
                "{\"3\":{\"class_type\":\"X\",\"inputs\":false}}")) {
            assertWorkflowError(bad, "3", "inputs");
        }
    }

    /**
     * isLinkedInput / getLink 的协议形状：只有 [非空文本节点ID, 可转 int 的非负整数] 才是链接。
     */
    @Test
    public void testIsLinkedInputAcceptsOnlyValidTwoElementArrays() {
        Workflow wf = Workflow.fromApiJson(
                "{\"3\":{\"class_type\":\"KSampler\",\"inputs\":{}}}");
        WorkflowNode n = wf.getNode("3");
        ObjectNode inputs = n.getInputs();

        // 合法链接：IntNode 与可转 int 的 LongNode 都应生成字段完整的 NodeLink。
        ArrayNode valid0 = JsonNodeFactory.instance.arrayNode().add("4").add(0);
        ArrayNode validLong = JsonNodeFactory.instance.arrayNode().add("5").add((long) Integer.MAX_VALUE);
        inputs.set("ok_int0", valid0);
        inputs.set("ok_long_max", validLong);
        Assertions.assertTrue(n.isLinkedInput("ok_int0"));
        Assertions.assertTrue(n.isLinkedInput("ok_long_max"));
        NodeLink link0 = n.getLink("ok_int0").orElseThrow();
        Assertions.assertEquals("3", link0.getTargetNodeId());
        Assertions.assertEquals("ok_int0", link0.getTargetInputName());
        Assertions.assertEquals("4", link0.getSourceNodeId());
        Assertions.assertEquals(0, link0.getSourceOutputIndex());
        NodeLink linkMax = n.getLink("ok_long_max").orElseThrow();
        Assertions.assertEquals("3", linkMax.getTargetNodeId());
        Assertions.assertEquals("ok_long_max", linkMax.getTargetInputName());
        Assertions.assertEquals("5", linkMax.getSourceNodeId());
        Assertions.assertEquals(Integer.MAX_VALUE, linkMax.getSourceOutputIndex());

        // 负整数：可转 int 但 asInt() < 0，必须拒绝。
        assertNotLink(n, "neg", JsonNodeFactory.instance.arrayNode().add("4").add(-1));

        // 超出 int 范围的整数：isIntegralNumber=true 但 canConvertToInt=false。
        assertNotLink(n, "too_large",
                JsonNodeFactory.instance.arrayNode().add("4").add(2147483648L));
        assertNotLink(n, "too_small",
                JsonNodeFactory.instance.arrayNode().add("4").add(-2147483649L));

        // 小数：isIntegralNumber=false。
        assertNotLink(n, "decimal",
                JsonNodeFactory.instance.arrayNode().add("4").add(1.5));

        // 空白 / 空源节点 ID。
        assertNotLink(n, "blank_id",
                JsonNodeFactory.instance.arrayNode().add("   ").add(0));
        assertNotLink(n, "empty_id",
                JsonNodeFactory.instance.arrayNode().add("").add(0));

        // 长度错误：必须是二元数组。
        assertNotLink(n, "empty_array", JsonNodeFactory.instance.arrayNode());
        assertNotLink(n, "short", JsonNodeFactory.instance.arrayNode().add("4"));
        assertNotLink(n, "long", JsonNodeFactory.instance.arrayNode().add("4").add(0).add(1));

        // 类型错误：源节点必须文本，索引必须是整数。
        assertNotLink(n, "numeric_id", JsonNodeFactory.instance.arrayNode().add(4).add(0));
        assertNotLink(n, "boolean_id", JsonNodeFactory.instance.arrayNode().add(true).add(0));
        assertNotLink(n, "null_id", JsonNodeFactory.instance.arrayNode().addNull().add(0));
        assertNotLink(n, "string_index", JsonNodeFactory.instance.arrayNode().add("4").add("0"));
        assertNotLink(n, "boolean_index", JsonNodeFactory.instance.arrayNode().add("4").add(true));
        assertNotLink(n, "null_index", JsonNodeFactory.instance.arrayNode().add("4").addNull());

        // 非数组形态。
        assertNotLink(n, "plain_string", JsonNodeFactory.instance.textNode("hello"));
        assertNotLink(n, "plain_number", JsonNodeFactory.instance.numberNode(7));
        assertNotLink(n, "plain_object", JsonNodeFactory.instance.objectNode());
        assertNotLink(n, "plain_null", JsonNodeFactory.instance.nullNode());

        // 不存在的输入名同样返回 false / empty。
        Assertions.assertFalse(n.isLinkedInput("missing"));
        Assertions.assertTrue(n.getLink("missing").isEmpty());
    }

    private static void assertWorkflowError(String json, String nodeId, String requirement) {
        WorkflowException ex = Assertions.assertThrows(
                WorkflowException.class, () -> Workflow.fromApiJson(json));
        Assertions.assertTrue(ex.getMessage().contains(nodeId), ex.getMessage());
        Assertions.assertTrue(ex.getMessage().toLowerCase().contains(requirement), ex.getMessage());
    }

    private static void assertNotLink(WorkflowNode node, String inputName, JsonNode value) {
        node.getInputs().set(inputName, value);
        Assertions.assertFalse(node.isLinkedInput(inputName), inputName + " must not be a link");
        Assertions.assertTrue(node.getLink(inputName).isEmpty(), inputName + " must yield no NodeLink");
    }
}

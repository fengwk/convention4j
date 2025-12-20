package fun.fengwk.convention4j.comfyui.workflow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    public void testRandomizeSeed() {
        String json = "{\"3\": {\"class_type\": \"KSampler\", \"inputs\": {\"seed\": 123}}}";
        Workflow workflow = Workflow.fromApiJson(json);
        
        workflow.randomizeSeed();
        long newSeed = workflow.getNode("3").getInput("seed").asLong();
        Assertions.assertNotEquals(123L, newSeed);
    }
}
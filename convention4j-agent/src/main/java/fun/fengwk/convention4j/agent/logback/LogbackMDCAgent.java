package fun.fengwk.convention4j.agent.logback;

import com.alibaba.ttl.threadpool.agent.ConventionTransformer;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.JavassistTransformlet;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fengwk
 */
public class LogbackMDCAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        List<JavassistTransformlet> transformletList = new ArrayList<>();
        transformletList.add(new LogbackServiceProviderTransformlet());
        ClassFileTransformer transformer = new ConventionTransformer(transformletList);
        inst.addTransformer(transformer, true);
    }

}

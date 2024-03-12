package fun.fengwk.convention4j.agent;

import com.alibaba.ttl.threadpool.agent.TtlAgent;
import fun.fengwk.convention4j.agent.logback.LogbackMDCAgent;

import java.lang.instrument.Instrumentation;

/**
 * @author fengwk
 */
public class ConventionAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        TtlAgent.premain(agentArgs, inst);
        LogbackMDCAgent.premain(agentArgs, inst);
    }

}

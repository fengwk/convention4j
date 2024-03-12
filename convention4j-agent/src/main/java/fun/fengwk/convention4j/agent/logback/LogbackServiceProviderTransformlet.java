package fun.fengwk.convention4j.agent.logback;

import com.alibaba.ttl.threadpool.agent.internal.javassist.CannotCompileException;
import com.alibaba.ttl.threadpool.agent.internal.javassist.CtClass;
import com.alibaba.ttl.threadpool.agent.internal.javassist.CtMethod;
import com.alibaba.ttl.threadpool.agent.internal.javassist.NotFoundException;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.ClassInfo;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.JavassistTransformlet;

import java.io.IOException;
import java.util.Objects;

/**
 * @author fengwk
 */
public class LogbackServiceProviderTransformlet implements JavassistTransformlet {

    @Override
    public void doTransform(ClassInfo classInfo) throws IOException, NotFoundException, CannotCompileException {
        if (!Objects.equals(classInfo.getClassName(), "ch.qos.logback.classic.spi.LogbackServiceProvider")) {
            return;
        }

        CtClass providerClass = classInfo.getCtClass();
        CtMethod initializeMethod = providerClass.getDeclaredMethod("initialize");
        // @see ch.qos.logback.classic.spi.LogbackServiceProvider.initialize
        // @see fun.fengwk.convention4j.common.logback.TtlLogbackMDCAdapter
        initializeMethod.setBody("{\n" +
            "defaultLoggerContext = new ch.qos.logback.classic.LoggerContext();\n" +
            "        defaultLoggerContext.setName(ch.qos.logback.core.CoreConstants.DEFAULT_CONTEXT_NAME);\n" +
            "        initializeLoggerContext();\n" +
            "        defaultLoggerContext.start();\n" +
            "        markerFactory = new org.slf4j.helpers.BasicMarkerFactory();\n" +
            "        mdcAdapter = new fun.fengwk.convention4j.common.logback.TtlLogbackMDCAdapter();\n" +
            "        // set the MDCAdapter for the defaultLoggerContext immediately\n" +
            "        defaultLoggerContext.setMDCAdapter(mdcAdapter);\n" +
            "}");

        classInfo.setModified();
    }

}

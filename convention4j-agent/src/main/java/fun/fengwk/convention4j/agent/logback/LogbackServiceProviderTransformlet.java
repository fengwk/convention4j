package fun.fengwk.convention4j.agent.logback;

import com.alibaba.ttl.threadpool.agent.internal.javassist.*;
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

        // logback-classic 1.5.18

        CtClass ctClass = classInfo.getCtClass();

        // 1. 查找或创建无参构造函数
        CtConstructor defaultConstructor = null;
        for (CtConstructor c : ctClass.getDeclaredConstructors()) {
            if (c.getParameterTypes().length == 0) {
                defaultConstructor = c;
                break;
            }
        }

        // 使用 CtNewConstructor.defaultConstructor() 创建默认无参构造
        if (defaultConstructor == null) {
            defaultConstructor = CtNewConstructor.defaultConstructor(ctClass);
            ctClass.addConstructor(defaultConstructor);
        }

        // 2. 在super()调用后插入字段初始化代码
        defaultConstructor.insertAfter(
            "this.mdcAdapter = new fun.fengwk.convention4j.common.logback.TtlLogbackMDCAdapter();"
        );

        classInfo.setModified();
    }

}

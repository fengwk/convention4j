package com.alibaba.ttl.threadpool.agent;

import com.alibaba.ttl.threadpool.agent.internal.transformlet.JavassistTransformlet;

import java.util.List;

/**
 * @author fengwk
 */
public class ConventionTransformer extends TtlTransformer {

    public ConventionTransformer(List<? extends JavassistTransformlet> transformletList) {
        super(transformletList);
    }

}

package fun.fengwk.convention4j.common.idgen.uuid;

import fun.fengwk.convention4j.common.idgen.AbstractIdGenerator;
import fun.fengwk.convention4j.common.lifecycle.LifeCycleException;

import java.util.UUID;

/**
 * 该生成器将生成32位的UUID。
 * 
 * @author fengwk
 */
public class UUIDGenerator extends AbstractIdGenerator<String> {

    private static final char TRIM = '-';

    @Override
    protected String doNext() {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uuid.length(); i++) {
            if (uuid.charAt(i) != TRIM) {
                sb.append(uuid.charAt(i));
            }
        }
        return sb.toString();
    }

    @Override
    protected void doInit() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStart() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doStop() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doClose() throws LifeCycleException {
        // nothing to do
    }

    @Override
    protected void doFail() throws LifeCycleException {
        // nothing to do
    }

}

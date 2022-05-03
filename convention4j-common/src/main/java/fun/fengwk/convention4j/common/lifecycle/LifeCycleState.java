package fun.fengwk.convention4j.common.lifecycle;

import java.util.Objects;

/**
 * 生命周期状态。
 *
 * @author fengwk
 */
public enum LifeCycleState {

    /**
     * 新构建的对象状态。
     */
    NEW(0),

    /**
     * 初始化中，已调用{@link LifeCycle#init()}方法，但还未初始化完成。
     */
    INITIALIZING(1),

    /**
     * 已初始化，已调用{@link LifeCycle#init()}方法，并且初始化完成。
     */
    INITIALIZED(2),

    /**
     * 启动中，已调用{@link LifeCycle#start()}方法，但还未启动完成。
     */
    STARTING(3),

    /**
     * 已启动，已调用{@link LifeCycle#start()}方法，并且启动完成。
     */
    STARTED(4),

    /**
     * 停止中，已调用{@link LifeCycle#stop()}方法，但还未停止完成。
     */
    STOPPING(5),

    /**
     * 已停止，已调用{@link LifeCycle#stop()}方法，并且停止完成。
     */
    STOPPED(6),

    /**
     * 关闭中，已调用{@link LifeCycle#close()}方法，但还未关闭完成。
     */
    CLOSING(7),

    /**
     * 已关闭，已调用{@link LifeCycle#close()}方法，并且关闭完成。
     * 终态，已关闭状态无法转移到其它任何状态。
     */
    CLOSED(8),

    /**
     * 失败处理中，在{@link LifeCycle#init()}、{@link LifeCycle#start()}、
     * {@link LifeCycle#stop()}、{@link LifeCycle#close()}过程中发生异常将会进入失败中状态。
     * 终态，已失败状态无法转移到其它任何状态。
     */
    FAILING(9),

    /**
     * 已失败。
     * 终态，已失败状态无法转移到其它任何状态。
     */
    FAILED(10);

    private final int code;

    LifeCycleState(int code) {
        this.code = code;
    }

    /**
     * 使用状态码获取状态。
     *
     * @param code
     * @return
     */
    public static LifeCycleState of(Integer code) {
        if (code != null) {
            for (LifeCycleState e : values()) {
                if (Objects.equals(e.getCode(), code)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * 获取状态码。
     *
     * @return
     */
    public int getCode() {
        return code;
    }

}

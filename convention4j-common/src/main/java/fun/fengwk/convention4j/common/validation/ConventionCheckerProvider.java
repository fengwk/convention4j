package fun.fengwk.convention4j.common.validation;

import fun.fengwk.convention4j.common.OrderedObject;

/**
 * 检查其提供者。
 *
 * @author fengwk
 */
public interface ConventionCheckerProvider extends OrderedObject {

    /**
     * 仅获取当前数据的版本号。
     *
     * @return 版本号。
     */
    String version();

    /**
     * 获取提供的检查器视图。
     *
     * @return 检查器。
     */
    VersionCheckerList getVersionCheckerList();

}

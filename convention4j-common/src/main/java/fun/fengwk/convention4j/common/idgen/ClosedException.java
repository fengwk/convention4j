package fun.fengwk.convention4j.common.idgen;

/**
 * 该异常表示正在操作已经关闭的对象。
 *
 * @author fengwk
 */
public class ClosedException extends RuntimeException {

    public ClosedException(String message) {
        super(message);
    }

}

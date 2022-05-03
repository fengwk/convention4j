package fun.fengwk.convention4j.common;

/**
 * 顺序枚举。
 *
 * @author fengwk
 */
public enum Order {

    /**
     * 升序。
     */
    ASC {
        @Override
        public <E extends Comparable<E>> boolean isOrdered(E prev, E next) {
            return prev.compareTo(next) <= 0;
        }
    },

    /**
     * 降序。
     */
    DESC {
        @Override
        public <E extends Comparable<E>> boolean isOrdered(E prev, E next) {
            return prev.compareTo(next) >= 0;
        }
    };

    /**
     * 确定前后一对元素的顺序性。
     *
     * @return
     */
    public abstract <E extends Comparable<E>> boolean isOrdered(E prev, E next);

}

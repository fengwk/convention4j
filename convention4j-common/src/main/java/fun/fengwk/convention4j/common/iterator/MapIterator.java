package fun.fengwk.convention4j.common.iterator;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * 映射迭代器。
 *
 * @author fengwk
 */
class MapIterator<S, T> implements Iterator<T> {

    private final Iterator<S> iterator;
    private final Function<S, T> mapper;

    MapIterator(Iterator<S> iterator, Function<S, T> mapper) {
        this.iterator = Objects.requireNonNull(iterator);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        S next = iterator.next();
        return mapper.apply(next);
    }

}

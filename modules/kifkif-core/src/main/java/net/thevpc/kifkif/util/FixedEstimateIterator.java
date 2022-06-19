package net.thevpc.kifkif.util;

import java.util.Iterator;
import java.util.List;

public class FixedEstimateIterator<T> extends AbstractEstimateIterator<T> {
    private Iterator<T> base;
    private long max;
    private long consumed;

    public FixedEstimateIterator(List<T> base) {
        this(base.iterator(),base.size());
    }
    public FixedEstimateIterator(Iterator<T> base, int max) {
        this.base = base;
        this.max = max;
    }

    @Override
    public boolean hasNext() {
        return base.hasNext();
    }

    @Override
    public T next() {
        T n = base.next();
        consumed++;
        return n;
    }

    @Override
    public long consumedCount() {
        return consumed;
    }

    @Override
    public long remainingEstimateCount() {
        return max-consumed;
    }
}

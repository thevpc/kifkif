package net.thevpc.kifkif.util;

import net.thevpc.kifkif.EstimateIterator;

public abstract class AbstractEstimateIterator<T> implements EstimateIterator<T> {
    @Override
    public long totalEstimateCount() {
        return remainingEstimateCount() + consumedCount();
    }

    @Override
    public float progressRatio() {
        long t = totalEstimateCount();
        if (t == 0) {
            return 0;
        }
        return (float) (consumedCount() * 1.0 / t);
    }
}

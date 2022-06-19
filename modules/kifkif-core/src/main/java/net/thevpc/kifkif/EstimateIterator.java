package net.thevpc.kifkif;

import java.util.Iterator;

public interface EstimateIterator<T> extends Iterator<T> {
    long consumedCount();
    long remainingEstimateCount();
    long totalEstimateCount();
    float progressRatio();
}

package com.google.common.cache;

import com.hyperscalelogic.util.concurrent.StripedLocalLongAdder;

public final class StripedLocalStatsCounter implements StatsCounter {

    private final StripedLocalLongAdder hitCount = new StripedLocalLongAdder();
    private final StripedLocalLongAdder missCount = new StripedLocalLongAdder();
    private final StripedLocalLongAdder loadSuccessCount = new StripedLocalLongAdder();
    private final StripedLocalLongAdder loadExceptionCount = new StripedLocalLongAdder();
    private final StripedLocalLongAdder totalLoadTime = new StripedLocalLongAdder();
    private final StripedLocalLongAdder evictionCount = new StripedLocalLongAdder();

    public StripedLocalStatsCounter() {
    }

    @Override
    public void recordHits(int count) {
        hitCount.add(count);
    }

    @Override
    public void recordMisses(int count) {
        missCount.add(count);
    }

    @Override
    public void recordLoadSuccess(long loadTime) {
        loadSuccessCount.add(1);
        totalLoadTime.add(loadTime);
    }

    @Override
    public void recordLoadException(long loadTime) {
        loadExceptionCount.add(1);
        totalLoadTime.add(loadTime);
    }

    @Override
    public void recordEviction() {
        evictionCount.add(1);
    }

    @Override
    public CacheStats snapshot() {
        return new CacheStats(
                hitCount.sum(),
                missCount.sum(),
                loadSuccessCount.sum(),
                loadExceptionCount.sum(),
                totalLoadTime.sum(),
                evictionCount.sum());
    }

    public void incrementBy(StatsCounter other) {
        CacheStats otherStats = other.snapshot();
        hitCount.add(otherStats.hitCount());
        missCount.add(otherStats.missCount());
        loadSuccessCount.add(otherStats.loadSuccessCount());
        loadExceptionCount.add(otherStats.loadExceptionCount());
        totalLoadTime.add(otherStats.totalLoadTime());
        evictionCount.add(otherStats.evictionCount());
    }
}


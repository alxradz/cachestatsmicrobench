package com.google.common.cache;

import com.hyperscalelogic.util.concurrent.LocalStripedLongAdder;

public final class HomeBrewStatsCounter implements StatsCounter {


    private final LocalStripedLongAdder hitCount = new LocalStripedLongAdder();
    private final LocalStripedLongAdder missCount = new LocalStripedLongAdder();
    private final LocalStripedLongAdder loadSuccessCount = new LocalStripedLongAdder();
    private final LocalStripedLongAdder loadExceptionCount = new LocalStripedLongAdder();
    private final LocalStripedLongAdder totalLoadTime = new LocalStripedLongAdder();
    private final LocalStripedLongAdder evictionCount = new LocalStripedLongAdder();

    /**
     * Constructs an instance with all counts initialized to zero.
     */
    public HomeBrewStatsCounter() {
    }

    /**
     * @since 11.0
     */
    @Override
    public void recordHits(int count) {
        hitCount.add(count);
    }

    /**
     * @since 11.0
     */
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

    /**
     * Increments all counters by the values in {@code other}.
     */
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


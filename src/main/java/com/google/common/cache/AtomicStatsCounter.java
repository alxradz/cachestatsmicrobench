package com.google.common.cache;

import java.util.concurrent.atomic.AtomicLong;

public final class AtomicStatsCounter implements StatsCounter {
    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong missCount = new AtomicLong();
    private final AtomicLong loadSuccessCount = new AtomicLong();
    private final AtomicLong loadExceptionCount = new AtomicLong();
    private final AtomicLong totalLoadTime = new AtomicLong();
    private final AtomicLong evictionCount = new AtomicLong();

    /**
     * Constructs an instance with all counts initialized to zero.
     */
    public AtomicStatsCounter() {
    }

    /**
     * @since 11.0
     */
    @Override
    public void recordHits(int count) {
        hitCount.addAndGet(count);
    }

    /**
     * @since 11.0
     */
    @Override
    public void recordMisses(int count) {
        missCount.addAndGet(count);
    }

    @Override
    public void recordLoadSuccess(long loadTime) {
        loadSuccessCount.incrementAndGet();
        totalLoadTime.addAndGet(loadTime);
    }

    @Override
    public void recordLoadException(long loadTime) {
        loadExceptionCount.incrementAndGet();
        totalLoadTime.addAndGet(loadTime);
    }

    @Override
    public void recordEviction() {
        evictionCount.incrementAndGet();
    }

    @Override
    public CacheStats snapshot() {
        return new CacheStats(
                hitCount.longValue(),
                missCount.longValue(),
                loadSuccessCount.longValue(),
                loadExceptionCount.longValue(),
                totalLoadTime.longValue(),
                evictionCount.longValue());
    }

    /**
     * Increments all counters by the values in {@code other}.
     */
    public void incrementBy(StatsCounter other) {
        CacheStats otherStats = other.snapshot();
        hitCount.addAndGet(otherStats.hitCount());
        missCount.addAndGet(otherStats.missCount());
        loadSuccessCount.addAndGet(otherStats.loadSuccessCount());
        loadExceptionCount.addAndGet(otherStats.loadExceptionCount());
        totalLoadTime.addAndGet(otherStats.totalLoadTime());
        evictionCount.addAndGet(otherStats.evictionCount());
    }
}
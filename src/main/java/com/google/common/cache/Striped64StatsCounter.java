package com.google.common.cache;

public final class Striped64StatsCounter implements StatsCounter {
    private final LongAdder hitCount = new LongAdder();
    private final LongAdder missCount = new LongAdder();
    private final LongAdder loadSuccessCount = new LongAdder();
    private final LongAdder loadExceptionCount = new LongAdder();
    private final LongAdder totalLoadTime = new LongAdder();
    private final LongAdder evictionCount = new LongAdder();

    /**
     * Constructs an instance with all counts initialized to zero.
     */
    public Striped64StatsCounter() {
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
        loadSuccessCount.increment();
        totalLoadTime.add(loadTime);
    }

    @Override
    public void recordLoadException(long loadTime) {
        loadExceptionCount.increment();
        totalLoadTime.add(loadTime);
    }

    @Override
    public void recordEviction() {
        evictionCount.increment();
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
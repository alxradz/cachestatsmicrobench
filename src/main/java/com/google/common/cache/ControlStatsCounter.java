package com.google.common.cache;

public final class ControlStatsCounter implements StatsCounter {

    private long hitCount = 0;
    private long missCount = 0;
    private long loadSuccessCount = 0;
    private long loadExceptionCount = 0;
    private long totalLoadTime = 0;
    private long evictionCount = 0;

    public ControlStatsCounter() {
    }

    @Override
    public synchronized void recordHits(int count) {
        hitCount += count;
    }

    @Override
    public synchronized void recordMisses(int count) {
        missCount += count;
    }

    @Override
    public synchronized void recordLoadSuccess(long loadTime) {
        loadSuccessCount++;
        totalLoadTime += loadTime;
    }

    @Override
    public synchronized void recordLoadException(long loadTime) {
        loadExceptionCount++;
        totalLoadTime += loadTime;
    }

    @Override
    public synchronized void recordEviction() {
        evictionCount++;
    }

    @Override
    public synchronized CacheStats snapshot() {
        return new CacheStats(
                hitCount,
                missCount,
                loadSuccessCount,
                loadExceptionCount,
                totalLoadTime,
                evictionCount);
    }

    public synchronized void incrementBy(StatsCounter other) {
        final CacheStats otherStats = other.snapshot();

        hitCount += otherStats.hitCount();
        missCount += otherStats.missCount();
        loadSuccessCount += otherStats.loadSuccessCount();
        loadExceptionCount += otherStats.loadExceptionCount();
        totalLoadTime += otherStats.totalLoadTime();
        evictionCount += otherStats.evictionCount();
    }

    public synchronized void verify(StatsCounter other) {
        final CacheStats otherStats = other.snapshot();

        if (hitCount != otherStats.hitCount()) throw new IllegalStateException("Validation failed!");
        if (missCount != otherStats.missCount()) throw new IllegalStateException("Validation failed!");
        if (loadSuccessCount != otherStats.loadSuccessCount()) throw new IllegalStateException("Validation failed!");
        if (loadExceptionCount != otherStats.loadExceptionCount()) throw new IllegalStateException("Validation failed!");
        if (totalLoadTime != otherStats.totalLoadTime()) throw new IllegalStateException("Validation failed!");
        if (evictionCount != otherStats.evictionCount()) throw new IllegalStateException("Validation failed!");

    }
}
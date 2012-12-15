import com.google.common.cache.*;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheStatsMicroBench {

    private static final int[] samples = new int[256];

    public static void main(String[] args) throws InterruptedException {
        final Random rnd = new Random(0xCAFEBABE);
        for (int i = 0; i < 256; i++) {
            samples[i] = rnd.nextInt(2048);
        }

        int[] threads = {1, 2, 4, 8, 16};
        for (int j = 0; j < threads.length; j++) {
            final int thread = threads[j];
            final ExecutorService exec = Executors.newFixedThreadPool(thread);

            System.out.format("Threads=%d", thread).println();

            ControlStatsCounter control = new ControlStatsCounter();
            runTest("Control", thread, exec, control, control);

            runTest("Atomic1", thread, exec, new AtomicStatsCounter(), control);
            runTest("StripedAtomic1", thread, exec, new StripedAtomicStatsCounter(), control);
            runTest("StripedLock1", thread, exec, new StripedLockStatsCounter(), control);
            runTest("Striped641", thread, exec, new Striped64StatsCounter(), control);
            runTest("StripedLocal1", thread, exec, new StripedLocalStatsCounter(), control);

            runTest("Atomic2", thread, exec, new AtomicStatsCounter(), control);
            runTest("StripedAtomic2", thread, exec, new StripedAtomicStatsCounter(), control);
            runTest("StripedLock2", thread, exec, new StripedLockStatsCounter(), control);
            runTest("Striped642", thread, exec, new Striped64StatsCounter(), control);
            runTest("StripedLocal2", thread, exec, new StripedLocalStatsCounter(), control);

            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.HOURS);
        }
    }

    private static void runTest(Object desc, int thread, ExecutorService exec, StatsCounter subject, ControlStatsCounter control) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(thread);
        final long start = System.currentTimeMillis();
        for (int i = 0; i < thread; i++) {
            exec.execute(new BenchRunner(latch, subject));
        }
        latch.await();
        final long end = System.currentTimeMillis();
        System.out.format("  %-15s  Duration=%.4fs", desc, (end - start) / 1000.0).println();
        control.verify(subject);
    }

    private static class BenchRunner implements Runnable {


        private final CountDownLatch latch;
        private final StatsCounter subject;

        public BenchRunner(CountDownLatch latch, StatsCounter subject) {
            this.latch = latch;
            this.subject = subject;
        }

        @Override
        public void run() {
            int ptr = 0;

            for (int j = 0; j < 100000; j++) {

                for (int i = 0; i < 100; i++) {
                    subject.recordHits(samples[ptr % 256]);
                    ptr += 1;
                }

                for (int i = 0; i < 100; i++) {
                    subject.recordMisses(ptr % 256);
                    ptr += 1;
                }

                for (int i = 0; i < 100; i++) {
                    subject.recordLoadException(ptr % 256);
                    ptr += 1;
                }

                for (int i = 0; i < 100; i++) {
                    subject.recordLoadSuccess(ptr % 256);
                    ptr += 1;
                }

                for (int i = 0; i < 100; i++) {
                    subject.recordEviction();
                }

                subject.snapshot();
            }

            latch.countDown();
        }
    }
}

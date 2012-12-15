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

            CountDownLatch latch;
            long start;
            long end;
            StatsCounter subject;

            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            ControlStatsCounter control = new ControlStatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, control));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  Control            Duration=%.4fs", (end - start) / 1000.0).println();


            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            subject = new Striped64StatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, subject));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  Striped64          Duration=%.4fs", (end - start) / 1000.0).println();
            control.verify(subject);


            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            subject = new AtomicStatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, subject));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  Atomic             Duration=%.4fs", (end - start) / 1000.0).println();
            control.verify(subject);

            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            subject = new HomeBrewStatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, subject));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  LocalStripedLong   Duration=%.4fs", (end - start) / 1000.0).println();
            control.verify(subject);

            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            subject = new Striped64StatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, subject));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  Striped64          Duration=%.4fs", (end - start) / 1000.0).println();
            control.verify(subject);

            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            subject = new AtomicStatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, subject));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  Atomic             Duration=%.4fs", (end - start) / 1000.0).println();
            control.verify(subject);

            latch = new CountDownLatch(thread);
            start = System.currentTimeMillis();
            subject = new HomeBrewStatsCounter();
            for (int i = 0; i < thread; i++) {
                exec.execute(new BenchRunner(latch, subject));
            }
            latch.await();
            end = System.currentTimeMillis();
            System.out.format("  LocalStripedLong   Duration=%.4fs", (end - start) / 1000.0).println();
            control.verify(subject);

            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.HOURS);
        }

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

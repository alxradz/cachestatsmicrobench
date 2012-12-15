/*
   Copyright 2012 Alex Radeski

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.hyperscalelogic.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A high performance counter designed to be as non-intrusive as possible. It uses striped locks to increase bandwidth.
 * <p/>
 * This work with inspired by trying to emulate the behaviour of the Striped64 class by Doug Lea, but without using
 * the sun.misc.Unsafe class as at the time it was causing issues on the Android platform. To my surprise it performed
 * better across a number of thread configurations. Results available <a href="https://github.com/al3ks/cachestatsmicrobench">here</a>.
 *
 * @author Alex Radeski
 */
public final class StripedLockLongAdder {

    private static final int SIZE = 32;

    private final long[] adders = new long[SIZE];
    private final Object[] locks = new Object[SIZE];

    {
        for (int i = 0; i < SIZE; i++) locks[i] = new Object();
    }

    public final void add(long v) {
        final long tid = Thread.currentThread().getId();
        final int sid = (int) (tid ^ (tid >>> 32)) % SIZE;
        synchronized (locks[sid]) {
            adders[sid] += v;
        }
    }

    public final long sum() {
        long sum = 0;
        for (int i = 0; i < adders.length; i++) {
            long val = 0;
            synchronized (locks[i]) {
                val = adders[i];
            }
            sum += val;
        }
        return sum;
    }
}
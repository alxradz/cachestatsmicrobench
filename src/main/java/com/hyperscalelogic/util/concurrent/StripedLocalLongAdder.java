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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A high performance counter designed to be as non-intrusive as possible. It uses a thread-local
 * long reference to stripe counters across threads.
 * <p/>
 * This work with inspired by trying to emulate the behaviour of the Striped64 class by Doug Lea, but without using
 * the sun.misc.Unsafe class as at the time it was causing issues on the Android platform. To my surprise it performed
 * better across a number of thread configurations. Results available <a href="https://github.com/al3ks/cachestatsmicrobench">here</a>.
 *
 * @author Alex Radeski
 */
public final class StripedLocalLongAdder {

    private final List<LongRef> adders = new CopyOnWriteArrayList<LongRef>();

    private final ThreadLocal<LongRef> adder = new ThreadLocal<LongRef>() {
        protected LongRef initialValue() {
            final LongRef la = new LongRef();
            adders.add(la);
            return la;
        }
    };

    public final void add(long v) {
        adder.get().value += v;
    }

    public final long sum() {
        long sum = 0;
        for (int i = 0; i < adders.size(); i++) {
            sum += adders.get(i).value;
        }
        return sum;
    }

    private static final class LongRef {
        long value = 0;
    }
}
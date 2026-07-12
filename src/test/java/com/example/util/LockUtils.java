package com.example.util;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public final class LockUtils {

    private LockUtils() {
    }

    public static <T> T withLock(Lock lock, Supplier<T> action) {
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    public static void withLock(Lock lock, Runnable action) {
        withLock(lock, () -> {
            action.run();
            return null;
        });
    }
}

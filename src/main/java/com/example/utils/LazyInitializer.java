package com.example.utils;

import java.util.function.Supplier;

public class LazyInitializer<T> {

    private final Supplier<T> supplier;
    private T reference;

    public LazyInitializer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public boolean isInitialized() {
        return reference != null;
    }

    public T get() {
        if (reference == null) {
            synchronized (this) {
                if (reference == null) {
                    reference = supplier.get();
                }
            }
        }
        return reference;
    }

}

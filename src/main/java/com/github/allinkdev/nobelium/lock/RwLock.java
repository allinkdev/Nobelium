package com.github.allinkdev.nobelium.lock;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * A class that manages access to an inner field of type {@link T}. There may be an unlimited amount of read locks at one time, but only one write lock. Write locks may not exist if any read locks exist.
 *
 * @param <T> The type of the inner variable
 */
public final class RwLock<T> {
    private final AtomicLong idCounter = new AtomicLong();
    private final Set<Guard<T>> readLocks = Collections.synchronizedSet(new LinkedHashSet<>());
    private T inner;
    private Guard<T> writeLock;

    RwLock(final T inner) {
        this.inner = inner;
    }

    public static <T> RwLock<T> create(final T inner) {
        return new RwLock<>(inner);
    }

    public CompletableFuture<Guard<T>> acquireWriteGuard() {
        return CompletableFuture.supplyAsync(() -> {
            while (this.writeLock != null || !this.readLocks.isEmpty()) {
                // Wait for the write lock to become available
            }

            this.writeLock = new Guard<>(-1, this.inner, g -> {
                this.inner = g.get();
                this.writeLock = null;
            });

            return this.writeLock;
        });
    }

    public CompletableFuture<Guard<T>> acquireReadGuard() {
        return CompletableFuture.supplyAsync(() -> {
            while (this.writeLock != null) {
                // Wait for writes to finish
            }

            final Guard<T> readLock = new Guard<>(this.idCounter.incrementAndGet(), this.inner, this.readLocks::remove);
            this.readLocks.add(readLock);

            return readLock;
        });
    }

    public static final class Guard<T> {
        public final long id;
        private final Consumer<Guard<T>> finishConsumer;
        private T value;

        Guard(final long id, final @NotNull T value, final @NotNull Consumer<Guard<T>> finishConsumer) {
            this.id = id;
            this.value = value;
            this.finishConsumer = finishConsumer;
        }

        public void set(final T newValue) {
            this.value = newValue;
        }

        public T get() {
            return this.value;
        }

        public void finished() {
            this.finishConsumer.accept(this);
        }
    }
}

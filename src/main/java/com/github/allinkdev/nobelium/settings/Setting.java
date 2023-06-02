package com.github.allinkdev.nobelium.settings;

import com.github.allinkdev.nobelium.lock.RwLock;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class Setting<T> {
    private final Class<? extends T> settingClass;
    private final SettingStore settingStore;
    private final RwLock<T> rwLock;

    @SuppressWarnings("unchecked")
    public Setting(final @NotNull T value, final @NotNull SettingStore settingStore) {
        this.rwLock = RwLock.create(value);
        this.settingClass = (Class<? extends T>) value.getClass();
        this.settingStore = settingStore;
    }

    public CompletableFuture<Void> set(final T newValue) {
        final Class<?> newValueClass = newValue.getClass();

        if (!this.settingClass.equals(newValueClass) && this.settingClass.isAssignableFrom(newValueClass)) {
            throw new IllegalStateException("Tried to set value of setting to an incompatible type! Expected " + this.settingClass.getName() + " while I was given " + newValueClass.getName());
        }

        return CompletableFuture.supplyAsync(() -> {
            final RwLock.Guard<T> writeLock = this.rwLock.acquireWriteGuard().join();
            writeLock.set(newValue);
            writeLock.finished();

            this.settingStore.save();

            return null;
        });
    }

    public CompletableFuture<T> get() {
        return CompletableFuture.supplyAsync(() -> {
            final RwLock.Guard<T> readLock = this.rwLock.acquireReadGuard().join();
            final T inner = readLock.get();
            readLock.finished();

            return inner;
        });
    }

    public Setting<T> deserialize(final Gson gson, final JsonElement json, final SettingStore settingStore) throws JsonParseException {
        return new Setting<>(gson.fromJson(json, settingClass), settingStore);
    }

    public JsonElement serialize(final JsonSerializationContext context) {
        return context.serialize(this.get().join());
    }
}

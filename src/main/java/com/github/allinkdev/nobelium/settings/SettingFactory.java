package com.github.allinkdev.nobelium.settings;

public final class SettingFactory {
    private final SettingStore settingStore;

    public SettingFactory(final SettingStore settingStore) {
        this.settingStore = settingStore;
    }

    public <T> Setting<T> create(final T defaultValue) {
        return new Setting<>(defaultValue, this.settingStore);
    }
}

package com.github.allinkdev.nobelium.settings.enums;

public enum TypeOfService {
    Routine(0x00),
    Priority(0x08),
    Immediate(0x10),
    Flash(0x18),
    FlashOverride(0x20),
    Critical(0x28);

    public final int value;

    TypeOfService(final int value) {
        this.value = value;
    }
}

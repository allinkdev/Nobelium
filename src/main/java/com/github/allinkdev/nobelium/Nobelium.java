package com.github.allinkdev.nobelium;

import com.github.allinkdev.nobelium.network.channel.ChannelConfigModifiers;
import com.github.allinkdev.nobelium.settings.SettingStore;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class Nobelium implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nobelium");
    private SettingStore settingStore;

    @Override
    public void onInitializeClient() {
        try {
            this.settingStore = SettingStore.createNew().deserializeInto();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize config!", e);
        }

        this.settingStore.fastOpen.get().thenAccept(val ->
                LOGGER.info("fast open: {}", val));
        this.settingStore.typeOfService.get().thenAccept(val ->
                LOGGER.info("type of service: {}", val));

        ChannelConfigModifiers.init(this.settingStore);
    }
}

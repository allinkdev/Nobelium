package com.github.allinkdev.nobelium.network.channel;

import com.github.allinkdev.nobelium.network.channel.impl.FastOpenConnectModifier;
import com.github.allinkdev.nobelium.network.channel.impl.ToSModifier;
import com.github.allinkdev.nobelium.settings.SettingStore;
import io.netty.channel.ChannelConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ChannelConfigModifiers {
    private static final Set<ChannelConfigModifier<?, ?>> MODIFIERS = new LinkedHashSet<>();

    private ChannelConfigModifiers() {
        //
    }

    public static void init(final SettingStore settingStore) {
        MODIFIERS.add(new ToSModifier(settingStore));
        MODIFIERS.add(new FastOpenConnectModifier(settingStore));
    }

    public static void modify(final ChannelConfig channelConfig) {
        MODIFIERS.forEach(c -> c.modify(channelConfig));
    }
}

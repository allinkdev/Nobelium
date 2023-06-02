package com.github.allinkdev.nobelium.network.channel.impl;

import com.github.allinkdev.nobelium.network.channel.ChannelConfigModifier;
import com.github.allinkdev.nobelium.settings.SettingStore;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;

public final class KeepAliveModifier extends ChannelConfigModifier<Boolean, Boolean> {
    public KeepAliveModifier(final SettingStore settingStore) {
        super(ChannelOption.SO_KEEPALIVE, settingStore.disableSocketKeepAlive);
    }

    @Override
    protected void modify(final ChannelConfig channelConfig, final ChannelOption<Boolean> option, final Boolean settingValue) {
        channelConfig.setOption(option, !settingValue);
    }
}

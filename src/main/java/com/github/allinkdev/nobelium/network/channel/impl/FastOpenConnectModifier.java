package com.github.allinkdev.nobelium.network.channel.impl;

import com.github.allinkdev.nobelium.network.channel.ChannelConfigModifier;
import com.github.allinkdev.nobelium.settings.SettingStore;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;

public final class FastOpenConnectModifier extends ChannelConfigModifier<Boolean, Boolean> {
    public FastOpenConnectModifier(final SettingStore settingStore) {
        super(ChannelOption.TCP_FASTOPEN_CONNECT, settingStore.fastOpen);
    }

    @Override
    protected void modify(final ChannelConfig channelConfig, final ChannelOption<Boolean> option, final Boolean settingValue) {
        channelConfig.setOption(option, settingValue);
    }
}

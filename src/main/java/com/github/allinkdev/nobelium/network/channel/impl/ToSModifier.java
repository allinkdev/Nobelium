package com.github.allinkdev.nobelium.network.channel.impl;

import com.github.allinkdev.nobelium.network.channel.ChannelConfigModifier;
import com.github.allinkdev.nobelium.settings.SettingStore;
import com.github.allinkdev.nobelium.settings.enums.TypeOfService;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;

public final class ToSModifier extends ChannelConfigModifier<Integer, TypeOfService> {
    public ToSModifier(final SettingStore settingStore) {
        super(ChannelOption.IP_TOS, settingStore.typeOfService);
    }

    @Override
    protected void modify(final ChannelConfig channelConfig, final ChannelOption<Integer> option, final TypeOfService settingValue) {
        channelConfig.setOption(option, settingValue.value);
    }
}

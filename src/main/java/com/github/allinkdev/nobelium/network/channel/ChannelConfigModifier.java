package com.github.allinkdev.nobelium.network.channel;

import com.github.allinkdev.nobelium.settings.Setting;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;

public abstract class ChannelConfigModifier<T, S> {
    private final ChannelOption<T> option;
    private final Setting<S> setting;

    protected ChannelConfigModifier(final ChannelOption<T> option, final Setting<S> setting) {
        this.option = option;
        this.setting = setting;
    }

    public boolean modify(final ChannelConfig channelConfig) {
        try {
            this.modify(channelConfig, this.option, setting.get().join());
        } catch (Throwable ex) {
            return false;
        }

        return true;
    }

    protected abstract void modify(final ChannelConfig channelConfig, final ChannelOption<T> option, final S settingValue);
}

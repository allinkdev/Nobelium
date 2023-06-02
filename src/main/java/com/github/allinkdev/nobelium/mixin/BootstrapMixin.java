package com.github.allinkdev.nobelium.mixin;

import com.github.allinkdev.nobelium.network.channel.ChannelConfigModifiers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/ClientConnection$1", remap = false)
final class BootstrapMixin {
    @Inject(method = "initChannel", at = @At("HEAD"))
    private void onInitChannel(final Channel channel, final CallbackInfo ci) {
        final ChannelConfig channelConfig = channel.config();

        ChannelConfigModifiers.modify(channelConfig);
    }
}

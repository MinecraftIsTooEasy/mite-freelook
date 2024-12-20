package com.github.jeffyjamzhd.freelook.mixins;

import com.github.jeffyjamzhd.freelook.FreeLookAddon;
import com.github.jeffyjamzhd.freelook.event.CameraEvent;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public GameSettings gameSettings;

    // Blocks F8, in a shitty fashion
    @Inject(method = "runTick", at = @At(value = "TAIL"))
    private void blockF5F8(CallbackInfo ci) {
        if (!FreeLookAddon.enableF5) this.gameSettings.thirdPersonView = 0;
        this.gameSettings.smoothCamera = false;
    }

    // Removes mirrored third person mode (front facing cam)
    @Inject(method = "runTick", at = @At(value = "TAIL"))
    private void wrapF5(CallbackInfo ci) {
        if (this.gameSettings.thirdPersonView > 1) {
            this.gameSettings.thirdPersonView = 0;
        }
    }
}

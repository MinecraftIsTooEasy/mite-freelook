package com.github.jeffyjamzhd.freelook.mixins;

import com.github.jeffyjamzhd.freelook.FreeLookAddon;
import com.github.jeffyjamzhd.freelook.event.CameraEvent;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 2000)
public abstract class MinecraftMixin {
    @Shadow public GameSettings gameSettings;

    // Runs initialization
    @Inject(method = "startGame", at = @At("RETURN"))
    private void startGame(CallbackInfo ci) {
        FreeLookAddon.getInstance().initializeKeybinds();
    }

    // Checks for alt look
    @Inject(method = "runTick", at = @At(value = "HEAD"))
    private void onClientTick(CallbackInfo ci) {
        CameraEvent.onClientTick();
    }

    // Blocks F5, in a shitty fashion
    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 14))
    private void blockF5(CallbackInfo ci) {
        if (!FreeLookAddon.enableF5)
            this.gameSettings.thirdPersonView = 0;
    }

    // Blocks F8, in a shitty fashion
    @Inject(method = "runTick", at = @At(value = "TAIL"))
    private void blockF8(CallbackInfo ci) {
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

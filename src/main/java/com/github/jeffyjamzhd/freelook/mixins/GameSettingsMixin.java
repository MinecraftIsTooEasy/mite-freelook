package com.github.jeffyjamzhd.freelook.mixins;

import com.github.jeffyjamzhd.freelook.FreeLookAddon;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameSettings.class)
public abstract class GameSettingsMixin {
    @Shadow public KeyBinding[] keyBindings;

    @Debug(export=true)
    @Inject(method = "<init>(Lnet/minecraft/Minecraft;Ljava/io/File;)V", at = @At(value = "TAIL"))
    private void addBinds(Minecraft par1Minecraft, File par2File, CallbackInfo ci) {
        // Init binds and load config
        // FreeLookAddon.getInstance().initializeKeybinds();
    }
}

package com.github.jeffyjamzhd.freelook.mixins;

import com.github.jeffyjamzhd.freelook.event.CameraEvent;
import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "renderItemInFirstPerson(F)V",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void moveViewmodel(float par1, CallbackInfo ci) {
        if (CameraEvent.flState != 0) {
            GL11.glRotatef(-CameraEvent.ogYaw - 180, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-CameraEvent.ogPitch, 1.0F, 0.0F, 0.0F);
        }
    }
}

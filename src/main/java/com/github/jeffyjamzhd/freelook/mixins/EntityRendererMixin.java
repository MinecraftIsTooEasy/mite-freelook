package com.github.jeffyjamzhd.freelook.mixins;

import com.github.jeffyjamzhd.freelook.event.CameraEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    // Update delta and zoom
    @Inject(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/Profiler;startSection(Ljava/lang/String;)V", ordinal = 1))
    private void updateZoom(CallbackInfo ci) {
        CameraEvent.updateDelta();
        CameraEvent.updateZoom();
    }

    @Inject(method = "setupCameraTransform(FIZ)V", at = @At(value = "TAIL"))
    private void updateLook(float par1, int par2, boolean extend_far_clipping_plane, CallbackInfo ci) {
        CameraEvent.updateFreelook(par1);
    }

    @Inject(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/EntityClientPlayerMP;setAngles(FF)V", ordinal = 1))
    private void lockLook(float par1, CallbackInfo ci) {
        if (CameraEvent.flState != 0) {
            // Get player
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;

            // Lock head rotation
            player.rotationYaw = CameraEvent.ogYaw;
            player.rotationPitch = CameraEvent.ogPitch;
        }
    }

    @Inject(method = "renderHand(FI)V", at = @At(value = "INVOKE",
            target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", shift = At.Shift.AFTER))
    private void rotateViewmodel(float par1, int par2, CallbackInfo ci) {
        CameraEvent.glRotateCam(1, par1);
    }

    @ModifyArg(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/EntityClientPlayerMP;setAngles(FF)V", ordinal = 0), index = 0)
    private float suppressMouseSmoothCamX(float x) {
        return CameraEvent.flState != 0 ? 0.0f : x * (float) CameraEvent.getCurZoom(); }

    @ModifyArg(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/EntityClientPlayerMP;setAngles(FF)V", ordinal = 0), index = 1)
    private float suppressMouseSmoothCamY(float y) {
        return CameraEvent.flState != 0 ? 0.0f : y * (float) CameraEvent.getCurZoom(); }

    @ModifyArg(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/EntityClientPlayerMP;setAngles(FF)V", ordinal = 1), index = 0)
    private float suppressMouseCamX(float x) {
        return CameraEvent.flState != 0 ? 0.0f : x * (float) CameraEvent.getCurZoom(); }

    @ModifyArg(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/EntityClientPlayerMP;setAngles(FF)V", ordinal = 1), index = 1)
    private float suppressMouseCamY(float y) {
        return CameraEvent.flState != 0 ? 0.0f : y * (float) CameraEvent.getCurZoom(); }

    @Inject(method = "getFOVModifier(FZ)F", at = @At("RETURN"), cancellable = true)
    private void setZoomLevel(float par1, boolean par2, CallbackInfoReturnable<Float> cir) {
        // Set zoom on FOV
        float fov = cir.getReturnValue();
        cir.setReturnValue(fov * (float) CameraEvent.getCurZoom());
    }
}

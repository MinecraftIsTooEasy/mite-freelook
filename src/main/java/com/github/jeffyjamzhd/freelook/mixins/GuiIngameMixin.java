package com.github.jeffyjamzhd.freelook.mixins;

import com.github.jeffyjamzhd.freelook.FreeLookAddon;
import com.github.jeffyjamzhd.freelook.event.CameraEvent;
import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin extends Gui {
    @Final
    @Shadow
    private Minecraft mc;

    @Unique
    private static final ResourceLocation squintTex = new ResourceLocation("textures/misc/squint.png");

    // Inject function into standard overlay render
    @Inject(method = "renderGameOverlay(FZII)V",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V", shift = At.Shift.AFTER, ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void addRenderGuiEnabled(float fSmoothCameraPartialTicks, boolean bScreenActive, int iMouseX, int iMouseY, CallbackInfo ci, ScaledResolution resolution, int iScreenWidth, int iScreenHeight, FontRenderer fontRenderer) {
        if (FreeLookAddon.zoomType == 0 && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
            renderSquint(this.mc.thePlayer.getBrightness(fSmoothCameraPartialTicks), iScreenWidth, iScreenHeight);
    }

    // Sets up GL color for crosshair
    @Inject(method = "renderGameOverlay(FZII)V",
              at = @At(value = "INVOKE",
                      target = "Lnet/minecraft/GuiIngame;drawTexturedModalRect(IIIIII)V",
                      ordinal = 2))
    private void crosshairGLBegin(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        float fac = 1.0F - (Math.min(CameraEvent.getZoomOverlayFac() + CameraEvent.getFreelookFac(), 1.0F));
        GL11.glColor4f(fac, fac, fac, 1.0F);
    }
    
    // Resets GL color after crosshair is rendered
    @Inject(method = "renderGameOverlay(FZII)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/GuiIngame;drawTexturedModalRect(IIIIII)V",
                    ordinal = 2, shift = At.Shift.AFTER))
    private void crosshairGLEnd(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // Squint overlay rendering (copied from vignette render)
    private void renderSquint(float par1, int par2, int par3) {
        par1 = 1.0F - par1;
        if (par1 < 0.0F) {
            par1 = 0.0F;
        }

        if (par1 > 1.0F) {
            par1 = 1.0F;
        }


        float overlayFac = CameraEvent.getZoomOverlayFac();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(0, 769);
        GL11.glColor4f(overlayFac, overlayFac, overlayFac, 1.0F);
        this.mc.getTextureManager().bindTexture(squintTex);
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        var4.addVertexWithUV(0.0F, par3, -90.0F, 0.0F, 1.0F);
        var4.addVertexWithUV(par2, par3, -90.0F, 1.0F, 1.0F);
        var4.addVertexWithUV(par2, 0.0F, -90.0F, 1.0F, 0.0F);
        var4.addVertexWithUV(0.0F, 0.0F, -90.0F, 0.0F, 0.0F);
        var4.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(770, 771);
    }
}

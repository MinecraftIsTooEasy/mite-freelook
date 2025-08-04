package com.github.jeffyjamzhd.freelook.event;

import com.github.jeffyjamzhd.freelook.FreeLookAddon;
import net.minecraft.*;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class CameraEvent {
    /*
        @todo
        - Figure out a better dt calculation using ticks
        - Figure out adapting viewbob code when freelooking,
          it is currently incorrect and does not line up
        - Add in an optional fullbody view, as per Ivan's request, which is
          maybe what I should've done first before doing viewmodel
          shenanigans.. hindsight is 20/20
        - Optimize and clean up this code
     */

    public static boolean zoomToggled = false;
    public static float ogYaw, ogPitch;
    private static double curZoom = 1.0;
    private static double tarZoom = 1.0;
    private static float zoomOverlayFac = 0.0F;
    private static float flFac = 0.0F;
    private static float zoomSpeed = 12.0F;
    private static long last;
    private static float delta;

    public static final int FL_OFF = 0;
    public static final int FL_LERP = 1;
    public static final int FL_ON = 2;
    public static int flState = FL_OFF;
    private static double mdeltaX, mdeltaY;
    public static float yaw, pitch, prevYaw, prevPitch;
    private static long lerpStart = 0, lerpTime = 0;

    // Sets zoom toggle, among other values
    public static void setZoom(boolean toggle) {
        zoomToggled = toggle;
        if (FreeLookAddon.smoothZoom) getMinecraft().gameSettings.smoothCamera = toggle;
    }

    // Sets freelook state
    public static void setFreelook(int state) {
        flState = state;
        if (flState == FL_ON) {
            ogPitch = getMinecraft().thePlayer.rotationPitch;
            ogYaw = getMinecraft().thePlayer.rotationYaw;
            yaw = ogYaw - 180; pitch = ogPitch;
            prevYaw = ogYaw - 180; prevPitch = ogPitch;
        } else if (flState == FL_OFF) {
            yaw = 0.0f; ogYaw = 0.0f;
            pitch = 0.0f; ogPitch = 0.0f;
            lerpTime = 0; lerpStart = 0;
        } else {
            lerpStart = System.currentTimeMillis();
        }
    }

    // Updates zoom FOV
    public static void updateZoom() {
        float realZoom = (FreeLookAddon.zoomFactor * 800.0f + 200.0f) / 100.0f;
        boolean instantZoom = FreeLookAddon.zoomType == 2;
        if (!instantZoom) {
            tarZoom = (!zoomToggled) ? 1.0 : (1.0 / realZoom);
            curZoom = curZoom + (tarZoom - curZoom) * (zoomSpeed * delta);
            zoomOverlayFac = (zoomOverlayFac + (((!zoomToggled) ? 0.0F : 1.0F) - zoomOverlayFac) * (zoomSpeed * delta));
        } else {
            curZoom = !zoomToggled ? 1.0 : (1.0 / realZoom);
            zoomOverlayFac = !zoomToggled ? 0.0F : 1.0F;
        }

    }

    // Updates freelook
    public static void updateFreelook(float par1) {
        if (flState == FL_ON && Display.isActive() && getMinecraft().inGameHasFocus) {
            updateMouse();
            updateCameraRot(par1);
        } else if (flState == FL_LERP) {
            // Interpolate angles
            float percent = (float) lerpTime / 200.0f;
            float percentDT = (System.currentTimeMillis() - lerpStart) - lerpTime;
            percentDT /= 200.0f;

            yaw = yaw + ((getMinecraft().thePlayer.rotationYaw - 180) - yaw) * (percent * 10f * percentDT);
            pitch = pitch + (getMinecraft().thePlayer.rotationPitch - pitch) * (percent * 10f * percentDT);
            glRotateCam(0, par1);

            // Update lerp time
            lerpTime = System.currentTimeMillis() - lerpStart;

            // if over duration, stop
            if (lerpTime >= 200.0f)
                setFreelook(FL_OFF);
        }
        flFac = (flFac + (((flState != FL_ON) ? 0.0F : 1.0F) - flFac) * (12.0F * delta));
    }

    // Updates mouse input
    private static void updateMouse() {
        float sens = getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        sens = sens * sens * sens * 4.0F;

        float overall_speed_modifier = getMinecraft().thePlayer.getSpeedBoostVsSlowDown();
        if (overall_speed_modifier < 0.0F) {
            if (overall_speed_modifier < -0.8F)
                overall_speed_modifier = -0.8F;
            sens /= 1.0F - overall_speed_modifier * 15.0F;
        }

        mdeltaX = getMinecraft().mouseHelper.deltaX * sens * (float) curZoom;
        mdeltaY = getMinecraft().mouseHelper.deltaY * sens * (float) curZoom;
    }

    // Updates camera rotation
    public static void updateCameraRot(float par1) {
        // Calculate yaw and pitch
        yaw = (float) (prevYaw + mdeltaX * 0.15D);
        pitch = (float) (getMinecraft().gameSettings.invertMouse ? prevPitch + mdeltaY * 0.15D : prevPitch - mdeltaY * 0.15D);

        // Clamp if in first person
        float realRange = FreeLookAddon.freelookRange * 135.0f + 45.0f;
        if (getMinecraft().gameSettings.thirdPersonView == 0 && realRange != 180.0F) {
            yaw = MathHelper.clamp_float(yaw, (ogYaw - 180) - realRange, (ogYaw - 180) + realRange);
            pitch = MathHelper.clamp_float(pitch, ogPitch - realRange / 2, ogPitch + realRange / 2);
        } else {
            if (yaw > (ogYaw - 180) + 180) yaw = ((ogYaw - 180) - 180) + (float) (mdeltaX * 0.15D);
            if (yaw < (ogYaw - 180) - 180) yaw = ((ogYaw - 180) + 180) + (float) (mdeltaX * 0.15D);
        }

        pitch = MathHelper.clamp_float(pitch, -90.0f, 90.0f);
        glRotateCam(0, par1);

        // Set prev
        prevYaw = yaw;
        prevPitch = pitch;
    }

    // @todo
    //  there are probably a lot of redundant calculations in here
    public static void glRotateCam(int mode, float par1) {
        GameSettings gs = getMinecraft().gameSettings;
        Minecraft m = getMinecraft();
        switch (mode) {
            // Main cam rotation
            case 0:
                GL11.glLoadIdentity();

                // Get view bobbing values
                EntityPlayer p = (EntityPlayer) getMinecraft().renderViewEntity;
                float dist = p.distanceWalkedModified - p.prevDistanceWalkedModified;
                float fac = -(p.distanceWalkedModified + dist * par1);
                float var5 = p.prevCameraYaw + (p.cameraYaw - p.prevCameraYaw) * par1;
                float var6 = p.prevCameraPitch + (p.cameraPitch - p.prevCameraPitch) * par1;
                GL11.glTranslatef(
                        gs.viewBobbing ? MathHelper.sin(fac * (float)Math.PI) * var5 * 0.5F : 0.0F,
                        gs.viewBobbing ? -Math.abs(MathHelper.cos(fac * (float)Math.PI) * var5) : 0.0F,
                        0.0F
                );
                if (gs.viewBobbing) {
                    GL11.glRotatef(MathHelper.sin(fac * (float)Math.PI) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(Math.abs(MathHelper.cos(fac * (float)Math.PI - 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
                }

                // Third person handling
                float camDist = -0.1f;
                if (gs.thirdPersonView > 0) {
                    EntityLivingBase var2 = m.renderViewEntity;
                    float height = var2.yOffset - 1.62F;
                    double x = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)par1;
                    double y = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)par1 - (double)height;
                    double z = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)par1;
                    double lookDist = 4.0f;

                    double var14 = (double)(-MathHelper.sin((yaw - 180) / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI)) * lookDist;
                    double var16 = (double)(MathHelper.cos((yaw - 180) / 180.0F * (float)Math.PI) * MathHelper.cos(pitch / 180.0F * (float)Math.PI)) * lookDist;
                    double var18 = (double)(-MathHelper.sin(pitch / 180.0F * (float)Math.PI)) * lookDist;

                    for(int var20 = 0; var20 < 8; ++var20) {
                        float var21 = (float)((var20 & 1) * 2 - 1);
                        float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
                        float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
                        var21 *= 0.1F;
                        var22 *= 0.1F;
                        var23 *= 0.1F;

                        RaycastCollision var24 = m.theWorld.getBlockCollisionForPolicies(m.theWorld.getVec3(
                                x + (double)var21, y + (double)var22, z + (double)var23),
                                m.theWorld.getVec3(x - var14 + (double)var21 + (double)var23,
                                        y - var18 + (double)var22, z - var16 + (double)var23),
                                RaycastPolicies.for_third_person_view);
                        if (var24 != null) {
                            double var25 = var24.position_hit.distanceTo(m.theWorld.getWorldVec3Pool().getVecFromPool(x, y, z));
                            if (var25 < lookDist) {
                                lookDist = var25;
                            }
                        }
                    }
                    camDist = gs.thirdPersonView == 2 ? (float) lookDist : (float) -lookDist;

                }

                // Make final transforms
                GL11.glTranslatef(0.0f, 0.0f, camDist);
                GL11.glRotatef(pitch + (gs.thirdPersonView == 2 ? 180.0F : 0.0F), 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
                break;
            // Viewmodel cam rotation
            case 1:
                if (flState != 0) {
                    GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
                }
                break;
        }
    }

    // Updates delta time
    public static void updateDelta() {
        long now = System.nanoTime();
        long elapsed = now - last;
        last = now;
        delta = elapsed / 1000000000.0f;
    }

    // Obtains values
    private static float getFov() {
        return getMinecraft().gameSettings.fovSetting;
    }

    // Obtains instance of minecraft
    private static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    // Obtains current zoom value
    public static double getCurZoom() {
        return curZoom;
    }

    // Obtains current zoom factor (for overlay)
    public static float getZoomOverlayFac() { return zoomOverlayFac; }

    // Obtains current freelook factor (for... things!)
    public static float getFreelookFac() { return flFac; }


}

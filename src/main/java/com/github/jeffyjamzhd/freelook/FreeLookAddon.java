package com.github.jeffyjamzhd.freelook;

import com.github.jeffyjamzhd.freelook.config.Configs;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.ModResourceManager;

public class FreeLookAddon implements ModInitializer {
    /*
        @todo
        - Clean up code
     */

    // Declare variables
    public static String modId = "freelook";
    public static int modVerNum = 1;
    public static String modVerStr = "v1.0.0";
    public static String modName = "MiTE Freelook";

    private static FreeLookAddon instance;
    public static IKeybind zoom_key, freelook_key;
    public static float freelookRange = 0.6f, zoomFactor = 0.4f;
    public static boolean enableF5 = true, smoothZoom = false;
    public static int zoomType = 0;

    public FreeLookAddon() {
        super();
    }

    @Override
    public void onInitialize() {
        ModResourceManager.addResourcePackDomain(modId);
        Configs.getInstance().load();
        ConfigManager.getInstance().registerConfig(Configs.getInstance());
    }

    public static FreeLookAddon getInstance() {
        return instance == null ? (new FreeLookAddon()) : instance;
    }

    public void handleConfigProperties() {
        zoom_key = Configs.keyZoom.getKeybind();
        freelook_key = Configs.keyFreelook.getKeybind();
        enableF5 = Configs.enableF5.getBooleanValue();
        smoothZoom = Configs.smoothZoom.getBooleanValue();
        zoomType = Configs.zoomType.getOrdinal();
        zoomFactor = ((float) Configs.zoomFactor.getDoubleValue() - 200.0f) / 800.0f;
        freelookRange = ((float) Configs.freelookRange.getDoubleValue() - 45.0f) / 135.0f;
    }
}
package com.github.jeffyjamzhd.freelook;

import com.github.jeffyjamzhd.freelook.config.Configs;
import fi.dy.masa.malilib.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.*;
import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.config.InjectionConfig;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public class FreeLookAddon extends AbstractMod implements ModInitializer {
    /*
        @todo
        - Rework how properties are handled, I dont think
          my way was ever the intent with BTW's config api
          (having values set at runtime)
        - Clean up code
     */

    // Declare variables
    public static String modId = "mitefreelook";
    public static int modVerNum = 1;
    public static String modVerStr = "v1.0.0";
    public static String modName = "MiTE Freelook";

    private static FreeLookAddon instance;
    public static KeyBinding freelook_key = new KeyBinding(StatCollector.translateToLocal("key.freelook"), Keyboard.KEY_LMENU);
    public static KeyBinding zoom_key = new KeyBinding(StatCollector.translateToLocal("key.zoom"), Keyboard.KEY_F);
    public static KeyBinding[] modBinds;
    public static float freelookRange = 0.6f, zoomFactor = 0.4f;
    public static boolean enableF5 = true, smoothZoom = false;
    public static int zoomType = 0;

    public FreeLookAddon() {
        super();
    }

    @Override
    public void preInit() {

    }

    @Override
    public @NotNull InjectionConfig getInjectionConfig() {
        return null;
    }

    @Override
    public String modId() {
        return modId;
    }

    @Override
    public int modVerNum() {
        return modVerNum;
    }

    @Override
    public String modVerStr() {
        return modVerStr;
    }

    @Override
    public void onInitialize() {
        Configs.getInstance().load();
        ConfigManager.getInstance().registerConfig(Configs.getInstance());
    }

    public static FreeLookAddon getInstance() {
        return instance == null ? (new FreeLookAddon()) : instance;
    }

    public void initializeKeybinds() {
        // Set descriptions
        freelook_key.keyDescription = "key.freelook";
        zoom_key.keyDescription = "key.zoom";

        // Add to array
        modBinds = new KeyBinding[]{zoom_key, freelook_key};
    }

//    public void handleConfigProperties(Map<String, String> propertyValues) {
//        zoom_key.keyCode = configRegistry.read("key.zoom");
//        freelook_key.keyCode = Integer.parseInt(propertyValues.get("key.freelook"));
//        enableF5 = Boolean.parseBoolean(propertyValues.get("enableF5"));
//        smoothZoom = Boolean.parseBoolean(propertyValues.get("smoothZoom"));
//        zoomType = Integer.parseInt(propertyValues.get("zoomType"));
//        zoomFactor = (Float.parseFloat(propertyValues.get("zoomFactor"))) / 800.0f;
//        freelookRange = (Float.parseFloat(propertyValues.get("freelookRange"))) / 135.0f;
//
//        properties = propertyValues;
//    }

//    // Handles slider value
//    public void setSliderConfig(String property, float value) {
//        switch (property) {
//            case "zoomFactor":
//                zoomFactor = value;
//                properties.put(property, String.valueOf(value * 800.0f));
//                break;
//            case "freelookRange":
//                freelookRange = value;
//                properties.put(property, String.valueOf(freelookRange * 135.0f));
//                break;
//        }
//
//    }

    // Gets slider display
    public String getSliderDisplay(String property) {
        switch (property) {
            case "zoomFactor":
                return zoomFactor != 1.0f ? String.format("%d%%", (int) ((zoomFactor * 800.0f) + 200.0f)) : "Eagle Eyed";
            case "freelookRange":
                return freelookRange != 1.0f ? (freelookRange * 135.0f) + 45.0f + " deg." : "Head On A Swivel";
        }
        return "";
    }
}
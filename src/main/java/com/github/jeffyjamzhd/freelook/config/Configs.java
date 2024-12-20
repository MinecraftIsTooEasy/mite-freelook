package com.github.jeffyjamzhd.freelook.config;

import com.github.jeffyjamzhd.freelook.FreeLookAddon;
import com.github.jeffyjamzhd.freelook.event.CameraEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigTab;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.StatCollector;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Configs extends SimpleConfigs {
    public static final ConfigBoolean enableF5 =
            new ConfigBoolean("freelook.enableF5", false,
                    "freelook.enableF5");
    public static final ConfigBoolean smoothZoom =
            new ConfigBoolean("freelook.smoothZoom", false,
                    "freelook.smoothZoom");
    public static final ConfigEnum<ZoomEnum> zoomType =
            new ConfigEnum<>("freelook.zoomType", ZoomEnum.REALISTIC,
                    "freelook.zoomType");
    public static final ConfigDoubleZF zoomFactor =
            new ConfigDoubleZF("freelook.zoomFactor", 400.0, 200.0, 1000.0,
                    "freelook.zoomFactor");
    public static final ConfigDoubleFR freelookRange =
            new ConfigDoubleFR("freelook.freelookRange", 90.0, 45.0, 180.0,
                    "freelook.freelookRange");
    public static final ConfigHotkey keyZoom =
            new ConfigHotkey("freelook.key.zoom", Keyboard.KEY_F);
    public static final ConfigHotkey keyFreelook =
            new ConfigHotkey("freelook.key.freelook", Keyboard.KEY_LMENU);

    private static final Configs instance;
    public static final List<ConfigBase<?>> general;
    public static final List<ConfigHotkey> hotkeys;

    public static final List<ConfigTab> tabs = new ArrayList<>();

    static {
        general = List.of(enableF5, smoothZoom, zoomType, zoomFactor, freelookRange);
        hotkeys = List.of(keyZoom, keyFreelook);

        KeybindSettings defaultKb = KeybindSettings.create(KeybindSettings.Context.INGAME, KeyAction.BOTH, true, true, false, true);
        keyZoom.getKeybind().setSettings(defaultKb);
        keyFreelook.getKeybind().setSettings(defaultKb);

        keyZoom.getKeybind().setCallback(((keyAction, iKeybind) -> {
            CameraEvent.setZoom(iKeybind.isKeybindHeld());
            return true;
        }));
        keyFreelook.getKeybind().setCallback(((keyAction, iKeybind) -> {
            if (CameraEvent.flState == CameraEvent.FL_OFF && iKeybind.isKeybindHeld()) {
                CameraEvent.setFreelook(CameraEvent.FL_ON);
            } else if (CameraEvent.flState == CameraEvent.FL_ON && !iKeybind.isKeybindHeld()) {
                CameraEvent.setFreelook(CameraEvent.FL_LERP);
            }
            return true;
        }));

        tabs.add(new ConfigTab("freelook.freelook", general));
        tabs.add(new ConfigTab("freelook.hotkeys", hotkeys));

        instance = new Configs();
    }

    public Configs() {
        super(
                "Freelook",
                hotkeys,
                general,
                "Keep your head on a swivel..."
        );
    }

    @Override
    public List<ConfigTab> getConfigTabs() {
        return tabs;
    }

    public static Configs getInstance() {
        return instance;
    }

    @Override
    public void save() {
        JsonObject root = new JsonObject();
        ConfigUtils.writeConfigBase(root, "freelook", general);
        ConfigUtils.writeConfigBase(root, "hotkeys", hotkeys);
        JsonUtils.writeJsonToFile(root, this.optionsFile);
        FreeLookAddon.getInstance().handleConfigProperties();
    }

    @Override
    public void load() {
        if (!this.optionsFile.exists()) {
            this.save();
        } else {
            JsonElement jsonElement = JsonUtils.parseJsonFile(this.optionsFile);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject root = jsonElement.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "freelook", general);
                ConfigUtils.readConfigBase(root, "hotkeys", hotkeys);
            }
            FreeLookAddon.getInstance().handleConfigProperties();
        }
    }

    public static class ConfigDoubleZF extends ConfigDouble {
        public ConfigDoubleZF(String name, double defaultValue, double minValue, double maxValue, String comment) {
            super(name, defaultValue, minValue, maxValue, true, comment);
        }
        @Override
        public String getDisplayText() {
            return String.format("%.0f", this.getDoubleValue()) + "%";
        }
        @Override
        public void setDoubleValue(double value) { super.setDoubleValue(roundToNotch(value, 16, this.getMinDoubleValue(), this.getMaxDoubleValue())); }

    }

    public static class ConfigDoubleFR extends ConfigDouble {
        public ConfigDoubleFR(String name, double defaultValue, double minValue, double maxValue, String comment) {
            super(name, defaultValue, minValue, maxValue, true, comment);
        }
        @Override
        public String getDisplayText() {
            return String.format("%.0f", this.getDoubleValue()) + StatCollector.translateToLocal("metric.degrees");
        }
        @Override
        public void setDoubleValue(double value) { super.setDoubleValue(roundToNotch(value, 9, this.getMinDoubleValue(), this.getMaxDoubleValue())); }
    }

    private static double roundToNotch(double value, int notches, double min, double max) {
        double fac = (max - min) / notches;
        double facMargin = fac / 2;
        for (int i = 0; i < notches; i++) {
            if (value - min < (fac * i) + facMargin) return (fac * i) + min;
            else if (value - min < fac * (i + 1)) return (fac * (i + 1)) + min;
        }
        return max;
    }
}

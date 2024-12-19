package com.github.jeffyjamzhd.freelook.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigTab;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.JsonUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Configs extends SimpleConfigs {
    public static final ConfigBoolean enableF5 =
            new ConfigBoolean("freelook.enableF5", false,
            "Enables the use of F5 camera modes, though is intended to be disabled. (DEFAULT: false)");
    public static final ConfigBoolean smoothZoom =
            new ConfigBoolean("freelook.smoothZoom", false,
            "Enables smooth camera movement on zoom. When disabled, camera sensitivity will be adjusted\n" +
                    "# to fit zoom percentage instead. (DEFAULT: false)");
    public static final ConfigEnum zoomType =
            new ConfigEnum("freelook.zoomType", ZoomEnum.REALISTIC,
            "Type of zoom to emulate. (DEFAULT: 0)\n" +
            "# 0 - \"Realistic\": Simulates effects of squinting (tunnel vision, slower zoom)\n" +
            "# 1 - Interpolated: Fast, interpolated zooming\n" +
            "# 2 - Classic: Optifine-like snap zoom");
    public static final ConfigDoubleZF zoomFactor =
            new ConfigDoubleZF("freelook.zoomFactor", 200.0, 200.0, 1000.0,
                    "Percentage to zoom in FOV by. (DEFAULT: 200.0)");
    public static final ConfigDoubleFR freelookRange =
            new ConfigDoubleFR("freelook.freelookRange", 45.0, 45.0, 180.0,
                    "Range of view in Freelook mode. (DEFAULT: 45.0 deg.)");
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

        tabs.add(new ConfigTab("freelook.freelook", general));
        tabs.add(new ConfigTab("freelook.hotkeys", hotkeys));

        instance = new Configs();
    }

    public Configs() {
        super("freelook.freelook", hotkeys, general, "freelook.desc");
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
        }
    }

    public static class ConfigDoubleZF extends ConfigDouble {
        public ConfigDoubleZF(String name, double defaultValue, double minValue, double maxValue, String comment) {
            super(name, defaultValue, minValue, maxValue, true, comment);
        }
        @Override
        public String getDisplayText() {
            return super.getStringValue() + "%";
        }
    }

    public static class ConfigDoubleFR extends ConfigDouble {
        public ConfigDoubleFR(String name, double defaultValue, double minValue, double maxValue, String comment) {
            super(name, defaultValue, minValue, maxValue, true, comment);
        }
        @Override
        public String getDisplayText() {
            return super.getStringValue() + " deg.";
        }
    }
}

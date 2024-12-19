package com.github.jeffyjamzhd.freelook.compat;

import com.github.jeffyjamzhd.freelook.config.Configs;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            return Configs.getInstance().getConfigScreen(screen);
        };
    }
}

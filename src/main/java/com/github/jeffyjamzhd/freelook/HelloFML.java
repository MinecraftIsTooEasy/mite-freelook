package com.github.jeffyjamzhd.freelook;

import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.reload.event.MITEEvents;

public class HelloFML implements ModInitializer {
    @Override
    public void onInitialize() {   //相当于main函数，万物起源
        MITEEvents.MITE_EVENT_BUS.register(new EventListen());
    }
}
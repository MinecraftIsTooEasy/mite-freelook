package com.github.jeffyjamzhd.freelook;

import com.google.common.eventbus.Subscribe;
import net.minecraft.ChatMessageComponent;
import net.minecraft.EntityPlayer;
import net.xiaoyu233.fml.reload.event.*;

public class EventListen {

    @Subscribe
    public void onItemRegister(ItemRegistryEvent event) {
    }

    @Subscribe
    public void onRecipeRegister(RecipeRegistryEvent event) {
    }

    @Subscribe
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
    }

    @Subscribe
    public void handleChatCommand(HandleChatCommandEvent event) {
    }
}

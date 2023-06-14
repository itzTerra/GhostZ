package com.terra.ghostz.event;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface LanternRemovedCallback {

    Event<LanternRemovedCallback> EVENT = EventFactory.createArrayBacked(LanternRemovedCallback.class,
        (listeners) -> (msg, stack, player) -> {
            for (LanternRemovedCallback listener : listeners) {
                ActionResult result = listener.action(msg, stack, player);
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult action(String msg, ItemStack stack, @Nullable PlayerEntity player);
}

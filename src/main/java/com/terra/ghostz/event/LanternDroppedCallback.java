package com.terra.ghostz.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface LanternDroppedCallback {
 
    Event<LanternDroppedCallback> EVENT = EventFactory.createArrayBacked(LanternDroppedCallback.class,
        (listeners) -> (itemstack, player) -> {
            for (LanternDroppedCallback listener : listeners) {
                ActionResult result = listener.action(itemstack, player);
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult action(ItemStack stack, PlayerEntity player);
}
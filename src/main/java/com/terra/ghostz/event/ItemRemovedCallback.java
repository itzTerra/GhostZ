package com.terra.ghostz.event;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemRemovedCallback {

    Event<ItemRemovedCallback> EVENT = EventFactory.createArrayBacked(ItemRemovedCallback.class,
        (listeners) -> (msg, stack, player) -> {
            for (ItemRemovedCallback listener : listeners) {
                ActionResult result = listener.action(msg, stack, player);
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult action(String msg, ItemStack stack, @Nullable PlayerEntity player);
}

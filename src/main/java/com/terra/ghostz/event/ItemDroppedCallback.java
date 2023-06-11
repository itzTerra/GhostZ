package com.terra.ghostz.event;

import com.terra.ghostz.GhostZ;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemDroppedCallback {
 
    Event<ItemDroppedCallback> EVENT = EventFactory.createArrayBacked(ItemDroppedCallback.class,
        (listeners) -> (itemstack, player) -> {
            for (ItemDroppedCallback listener : listeners) {
                ActionResult result = listener.action(itemstack, player);
                GhostZ.log(result.toString());
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult action(ItemStack stack, PlayerEntity player);
}
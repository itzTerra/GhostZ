package com.terra.ghostz.event;

import com.terra.ghostz.GhostZ;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemRemovedCallback {

    Event<ItemRemovedCallback> EVENT = EventFactory.createArrayBacked(ItemRemovedCallback.class,
        (listeners) -> (msg) -> {
            for (ItemRemovedCallback listener : listeners) {
                ActionResult result = listener.action(msg);
 
                if(result != ActionResult.PASS) {
                    return result;
                }
            }
 
        return ActionResult.PASS;
    });
 
    ActionResult action(String msg);
}

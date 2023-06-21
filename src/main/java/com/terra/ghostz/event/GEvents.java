package com.terra.ghostz.event;

import java.util.ArrayList;

import com.terra.ghostz.command.GhostLanternCommand;
import com.terra.ghostz.item.GhostLantern;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GEvents {

    public static void register(){
        // COMMANDS
        CommandRegistrationCallback.EVENT.register(GhostLanternCommand::register);


        // Reset GhostLantern wisp capacity
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ArrayList<ItemStack> lanterns = GhostLantern.lanternsInInventory(player.getInventory());

            for (ItemStack lantern : lanterns) {
                GhostLantern.suckWisps(lantern, origin);
            }
        });
        
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEntity player = handler.getPlayer();
            World world = player.getWorld();

            ArrayList<ItemStack> lanterns = GhostLantern.lanternsInInventory(player.getInventory());

            for (ItemStack lantern : lanterns) {
                GhostLantern.suckWisps(lantern, world);
            }
        });
    }
}

package com.terra.ghostz;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terra.ghostz.command.GhostLanternCommand;
import com.terra.ghostz.config.GConfig;
import com.terra.ghostz.item.GhostLantern;
import com.terra.ghostz.util.GRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GhostZ implements ModInitializer {
    public static final String MOD_ID = "ghostz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String CONFIG_PATH = FabricLoader.getInstance().getConfigDir() + "/ghostz.json";
	public static GConfig CONFIG;

    // public static final boolean TRINKETS_LOADED =
    // FabricLoader.getInstance().isModLoaded("trinkets");
    // not implementing it rn

    @Override
    public void onInitialize() {
        LOGGER.info("GhostZ initialization");

        CONFIG = GConfig.loadConfig(new File(CONFIG_PATH));

        GRegistry.init();

        CommandRegistrationCallback.EVENT.register(GhostLanternCommand::register);


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

    public static void log(String... messages) {
        LOGGER.info("(?) " + String.join(" ", messages));
    }

    public static void log(boolean useSep, String sep, String... messages) {
        if (useSep) {
            LOGGER.info("(?) " + String.join(sep, messages));
        } else {
            LOGGER.info("(?) " + sep + " " + String.join(" ", messages));
        }
    }

    public static void warn(String message) {
        LOGGER.warn("(!) " + message);
    }

    public static void error(String message) {
        LOGGER.error("(!!!) " + message);
    }
}
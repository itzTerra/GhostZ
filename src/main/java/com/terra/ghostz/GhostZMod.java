package com.terra.ghostz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostZMod implements ModInitializer {
    public static final String MOD_ID = "ghostz";
    public static final Logger LOGGER = LoggerFactory.getLogger("ghostz");

    public static final Block GHOST_LANTERN_BLOCK = new GhostLantern(FabricBlockSettings.copy(Blocks.SOUL_LANTERN));

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("GhostZ initialization");

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "ghost_lantern_block"), GHOST_LANTERN_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "ghost_lantern_block"), new BlockItem(GHOST_LANTERN_BLOCK, new FabricItemSettings().maxCount(1)));
    }
}
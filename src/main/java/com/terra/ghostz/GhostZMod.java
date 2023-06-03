package com.terra.ghostz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostZMod implements ModInitializer {
    public static final String MOD_ID = "ghostz";
    public static final Logger LOGGER = LoggerFactory.getLogger("ghostz");

    public static final GhostLanternBlock GHOST_LANTERN = new GhostLanternBlock(FabricBlockSettings.copy(Blocks.SOUL_LANTERN));

    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier("ghostz", "item_group"))
            .icon(() -> new ItemStack(GHOST_LANTERN))
            .build();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("GhostZ initialization");

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "ghost_lantern"), GHOST_LANTERN);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "ghost_lantern"), new BlockItem(GHOST_LANTERN, new FabricItemSettings().maxCount(1)));

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
            content.add(GHOST_LANTERN);
        });
    }
}
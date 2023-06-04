package com.terra.ghostz;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terra.ghostz.config.GConfig;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GhostZ implements ModInitializer {
    public static final String MOD_ID = "ghostz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static GConfig CONFIG;

    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "item_group"))
            .icon(() -> new ItemStack(GRegistry.GHOST_LANTERN_BLOCK)).build();

    public static final ArrayList<ItemConvertible> GROUP_ITEMS = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOGGER.info("GhostZ initialization");

		CONFIG = GConfig.loadConfig(new File(FabricLoader.getInstance().getConfigDir() + "/ghostz_config.json"));

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
            for (ItemConvertible item : GROUP_ITEMS) {
                content.add(item);
            }
        });
    }
}
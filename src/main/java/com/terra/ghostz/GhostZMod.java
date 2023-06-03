package com.terra.ghostz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhostZMod implements ModInitializer {
    public static final String MOD_ID = "ghostz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GhostLanternBlock GHOST_LANTERN_BLOCK = new GhostLanternBlock(FabricBlockSettings.copy(Blocks.SOUL_LANTERN));
    private static final Identifier GHOST_LANTERN_BLOCK_IDENTIFIER = new Identifier(MOD_ID, "ghost_lantern_block");
    public static final BlockEntityType<GhostLanternEntity> GHOST_LANTERN_ENTITY = FabricBlockEntityTypeBuilder
            .create(GhostLanternEntity::new, GHOST_LANTERN_BLOCK).build();


    public static final GhostLantern GHOST_LANTERN = new GhostLantern(new FabricItemSettings().maxDamage(10).rarity(Rarity.UNCOMMON).fireproof());




    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "item_group"))
            .icon(() -> new ItemStack(GHOST_LANTERN_BLOCK)).build();

    @Override
    public void onInitialize() {
        LOGGER.info("GhostZ initialization");

        Registry.register(Registries.BLOCK, GHOST_LANTERN_BLOCK_IDENTIFIER, GHOST_LANTERN_BLOCK);
        Registry.register(Registries.ITEM, GHOST_LANTERN_BLOCK_IDENTIFIER, new BlockItem(GHOST_LANTERN_BLOCK, new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON)));
        Registry.register(Registries.BLOCK_ENTITY_TYPE, GHOST_LANTERN_BLOCK_IDENTIFIER, GHOST_LANTERN_ENTITY);

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "ghost_lantern"), GHOST_LANTERN);

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
            content.add(GHOST_LANTERN);
            content.add(GHOST_LANTERN_BLOCK);
        });
    }
}
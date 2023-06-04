package com.terra.ghostz;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class GRegistry {

    public static final GhostLanternBlock GHOST_LANTERN_BLOCK = registerBlock("ghost_lantern_block",
            new GhostLanternBlock(FabricBlockSettings.copy(Blocks.SOUL_LANTERN)), new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON));

    public static final BlockEntityType<GhostLanternEntity> GHOST_LANTERN_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(GhostZ.MOD_ID, "ghost_lantern_block"),
            FabricBlockEntityTypeBuilder.create(GhostLanternEntity::new, GHOST_LANTERN_BLOCK).build());

    public static final Block WISP = registerBlock("wisp", new Block(FabricBlockSettings.of(Material.DECORATION).sounds(BlockSoundGroup.CANDLE)
            .noCollision().breakInstantly().dropsNothing().luminance(state -> {
                return GhostZ.CONFIG.levels.get(state.get(Wisp.LEVEL)-1).get("luminance");
            })));

    public static final GhostLantern GHOST_LANTERN = registerItem("ghost_lantern",
            new GhostLantern(WISP, new FabricItemSettings().maxDamage(GhostZ.CONFIG.getMaxWisps()).rarity(Rarity.UNCOMMON).fireproof()));


    public static <T extends Item> T registerItem(String id, T item) {
        GhostZ.GROUP_ITEMS.add(item);
        return Registry.register(Registries.ITEM, new Identifier(GhostZ.MOD_ID, id), item);
    }

    public static <T extends Block> T registerBlock(String id, T block) {
        return Registry.register(Registries.BLOCK, new Identifier(GhostZ.MOD_ID, id), block);
    }

    public static <T extends Block, S extends Item.Settings> T registerBlock(String id, T block, S itemSettings) {
        Registry.register(Registries.BLOCK, new Identifier(GhostZ.MOD_ID, id), block);
        registerItem(id, new BlockItem(block, itemSettings));
        return block;
    }
}

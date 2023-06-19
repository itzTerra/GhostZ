package com.terra.ghostz.util;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.block.GhostLanternBlock;
import com.terra.ghostz.block.Wisp;
import com.terra.ghostz.entity.GhostLanternBlockEntity;
import com.terra.ghostz.entity.WispEntity;
import com.terra.ghostz.item.GhostLantern;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class GRegistry {

    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(GhostZ.MOD_ID, "item_group"),
        () -> new ItemStack(GRegistry.GHOST_LANTERN_BLOCK));

    public static final GhostLanternBlock GHOST_LANTERN_BLOCK = registerBlock("ghost_lantern_block",
            new GhostLanternBlock(FabricBlockSettings.copy(Blocks.SOUL_LANTERN)), new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).group(ITEM_GROUP));

    public static final BlockEntityType<GhostLanternBlockEntity> GHOST_LANTERN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
            new Identifier(GhostZ.MOD_ID, "ghost_lantern_block"),
            FabricBlockEntityTypeBuilder.create(GhostLanternBlockEntity::new, GHOST_LANTERN_BLOCK).build());

    public static final Wisp WISP = registerBlock("wisp", new Wisp(FabricBlockSettings.of(Material.DECORATION).sounds(BlockSoundGroup.CANDLE)
            .noCollision().breakInstantly().dropsNothing().luminance(state -> {
                return GhostZ.CONFIG.levels.get(state.get(Wisp.LEVEL) - 1).get("luminance");
            })));
        
    public static final BlockEntityType<WispEntity> WISP_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
    new Identifier(GhostZ.MOD_ID, "wisp_entity"),
    FabricBlockEntityTypeBuilder.create(WispEntity::new, WISP).build());

    public static final GhostLantern GHOST_LANTERN = registerItem("ghost_lantern",
            new GhostLantern(WISP, new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).fireproof().group(ITEM_GROUP)));

    public static final DefaultParticleType WISP_GLITTER = Registry.register(Registry.PARTICLE_TYPE, new Identifier(GhostZ.MOD_ID, "wisp_glitter"),
    FabricParticleTypes.simple());


    
    public static <T extends Item> T registerItem(String id, T item) {
        GhostZ.GROUP_ITEMS.add(item);
        return Registry.register(Registry.ITEM, new Identifier(GhostZ.MOD_ID, id), item);
    }

    public static <T extends Block> T registerBlock(String id, T block) {
        return Registry.register(Registry.BLOCK, new Identifier(GhostZ.MOD_ID, id), block);
    }

    public static <T extends Block, S extends Item.Settings> T registerBlock(String id, T block, S itemSettings) {
        Registry.register(Registry.BLOCK, new Identifier(GhostZ.MOD_ID, id), block);
        registerItem(id, new BlockItem(block, itemSettings));
        return block;
    }

    public static void init() {
        GhostZ.log("Adding to registry...");
    }
}

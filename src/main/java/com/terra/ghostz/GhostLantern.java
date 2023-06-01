package com.terra.ghostz;

import java.util.List;

import com.mojang.brigadier.LiteralMessage;

import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GhostLantern extends LanternBlock{
    // protected static final VoxelShape STANDING_SHAPE = VoxelShapes.cuboid(5.0f, 0f, 5.0f, 11.0f, 9.0f, 11.0f);
    // protected static final VoxelShape HANGING_SHAPE = VoxelShapes.cuboid(5.0, 1.0, 5.0, 11.0, 10.0, 11.0);
    public static final BooleanProperty HANGING = Properties.HANGING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    
    protected static final VoxelShape STANDING_SHAPE = VoxelShapes.cuboid(0.2f, 0f, 0.2f, 0.8f, 0.8f, 0.8f);
    protected static final VoxelShape HANGING_SHAPE = VoxelShapes.cuboid(0.2f, 0.1f, 0.2f, 0.8f, 0.8f, 0.8f);
    
    public GhostLantern(Settings settings) {
        super(settings);
        setDefaultState((getDefaultState().with(HANGING, false)).with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return state.get(HANGING) != false ? HANGING_SHAPE : STANDING_SHAPE;
    }

    // @Override
    // public void appendTooltip(ItemStack itemStack, BlockView world, List<Text> tooltip, TooltipContext tooltipContext) {
    //     tooltip.add(Text.translatable("block.ghostz.ghost_lantern_block.tooltip").formatted(Formatting.YELLOW));
    // }
}

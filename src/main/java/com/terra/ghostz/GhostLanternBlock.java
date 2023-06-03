package com.terra.ghostz;

import java.util.List;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class GhostLanternBlock extends LanternBlock implements BlockEntityProvider{
    protected static final VoxelShape STANDING_SHAPE = VoxelShapes.cuboid(0.22f, 0f, 0.22f, 0.78f, 0.7f, 0.78f);
    protected static final VoxelShape HANGING_SHAPE = VoxelShapes.cuboid(0.22f, 0.07f, 0.22f, 0.78f, 0.77f, 0.78f);

    public GhostLanternBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return state.get(HANGING) != false ? HANGING_SHAPE : STANDING_SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GhostLanternEntity(pos, state);
    }

    // @Override
    // public void appendTooltip(ItemStack itemStack, BlockView world, List<Text>
    // tooltip, TooltipContext tooltipContext) {
    // tooltip.add(Text.translatable("block.ghostz.ghost_lantern.tt_charged"));
    // tooltip.add(Text.translatable("block.ghostz.ghost_lantern.tt_bonuses"));

    // }
}

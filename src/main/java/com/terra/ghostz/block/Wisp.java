package com.terra.ghostz.block;

import com.terra.ghostz.GhostZ;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Wisp extends Block implements Waterloggable {
    public static final IntProperty LEVEL = IntProperty.of("wisp_level", 1, GhostZ.CONFIG.levels.size());
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public Wisp(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LEVEL, 1).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.40f, 0.40f, 0.40f, 0.60f, 0.60f, 0.60f);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        int level = ctx.getStack().getNbt().getInt("level");
        return (BlockState)this.getDefaultState()
            .with(LEVEL, level > 0 ? level : 1)
            .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
 
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()){
            if (placer.isPlayer()){
                placer.sendMessage(Text.of("NBT: "+ itemStack.getNbt().asString()+"\nBlockstate: "+state.toString()));
            }
        }
    }

}

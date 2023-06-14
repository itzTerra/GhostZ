package com.terra.ghostz.block;

import org.jetbrains.annotations.Nullable;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.entity.WispEntity;
import com.terra.ghostz.item.GhostLantern;
import com.terra.ghostz.util.GRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Wisp extends Block implements Waterloggable, BlockEntityProvider {
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WispEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.40f, 0.40f, 0.40f, 0.60f, 0.60f, 0.60f);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()){
            printInfo(player.getStackInHand(hand), state, player);
        }
        return ActionResult.SUCCESS;
    }

    public static void printInfo(ItemStack stack, BlockState state, @Nullable PlayerEntity player){
        if (player != null && player.isPlayer()){
            player.sendMessage(Text.literal("Blockstate: " + state.toString()).formatted(Formatting.GRAY));
        } else{
            GhostZ.log("=============== WISP INFO ==================\nBlockstate: " + state.toString());
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient()) {
            GhostLantern.addPosToNbt(itemStack, pos);

            WispEntity entity = (WispEntity) world.getBlockEntity(pos);
            // entity.lantern = itemStack;
            entity.playerID = placer.getUuid();
            entity.lanternID = GhostLantern.pingNBT(itemStack).getUuid(GhostLantern.ID_TAG);

            printInfo(itemStack, state, (PlayerEntity)placer);
        }
    }

    // NOT ON EXPLOSION, MAYBE EXPLOSION MIXIN?
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        WispEntity entity = (WispEntity) world.getBlockEntity(pos);
        if (!(entity instanceof WispEntity) || entity.playerID == null || entity.lanternID == null) {
            return;
        }
        PlayerEntity placer = world.getPlayerByUuid(entity.playerID);
        if (placer == null){
            return;
        }

        // placer.sendMessage(Text.literal("Saved ID: "+entity.lanternID));
        PlayerInventory inventory = placer.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.hasNbt()) continue;
            NbtCompound nbt = stack.getNbt();

            if (nbt.containsUuid(GhostLantern.ID_TAG) && nbt.getUuid(GhostLantern.ID_TAG).equals(entity.lanternID)) {
                // placer.sendMessage(Text.literal("Found:\n"+stack+" -- "+stack.getNbt()).formatted(Formatting.GREEN));
                GhostLantern.removePosFromNbt(stack, pos);
                break;
            } 
            // else{
            //     placer.sendMessage(Text.literal(""+nbt).formatted(Formatting.RED));
            // }
        }
    }

    // ############################ WATERLOG STUFF ##################################
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        // If wisp broke and waterlogged, spawn water
        if (!state.isOf(newState.getBlock()) && state.get(WATERLOGGED)) {
            world.setBlockState(pos, Blocks.WATER.getDefaultState(), NOTIFY_LISTENERS);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        int level = ctx.getStack().getNbt().getInt("level");
        return (BlockState) this.getDefaultState().with(LEVEL, level > 0 ? level : 1).with(WATERLOGGED,
                ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : state.getFluidState();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos,
            BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return state;
    }
    
    // PARTICLES
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = (double) pos.getX() + 0.55 - (double) (random.nextFloat() * 0.1f);
        double e = (double) pos.getY() + 0.60 - (double) (random.nextFloat() * 0.1f);
        double f = (double) pos.getZ() + 0.55 - (double) (random.nextFloat() * 0.1f);
        if (random.nextInt(5) == 0) {
            world.addParticle(GRegistry.WISP_GLITTER, d, e, f, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005,
                    random.nextGaussian() * 0.005);
        }

        // These are also nice
        // world.addParticle(ParticleTypes.SCRAPE, d, e, f, 0.0, 0.0, 0.0);
        // world.addParticle(ParticleTypes.GLOW, d, e, f, 0.0, 0.0, 0.0);
    }
}

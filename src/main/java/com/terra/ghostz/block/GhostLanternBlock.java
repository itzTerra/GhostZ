package com.terra.ghostz.block;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.entity.GhostLanternBlockEntity;
import com.terra.ghostz.item.GhostLantern;
import com.terra.ghostz.util.GRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GhostLanternBlock extends LanternBlock implements BlockEntityProvider {
    protected static final VoxelShape STANDING_SHAPE = VoxelShapes.cuboid(0.22f, 0f, 0.22f, 0.78f, 0.7f, 0.78f);
    protected static final VoxelShape HANGING_SHAPE = VoxelShapes.cuboid(0.22f, 0.07f, 0.22f, 0.78f, 0.77f, 0.78f);

    public static final IntProperty LEVEL = IntProperty.of("lantern_level", 1, GhostZ.CONFIG.levels.size());

    public GhostLanternBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LEVEL, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GhostLanternBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return state.get(HANGING) != false ? HANGING_SHAPE : STANDING_SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;

        BlockEntity blockEntity;
        if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(pos)) instanceof GhostLanternBlockEntity) {
            ((GhostLanternBlockEntity)blockEntity).setCustomName(itemStack.getName());
        }
    }

    // Debugging only
    // @Override
    // public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    //     if (!world.isClient){
    //         player.sendMessage(Text.literal("State"+state), false);

    //         BlockEntity blockEntity = world.getBlockEntity(pos);
    //         if (blockEntity instanceof GhostLanternBlockEntity){
    //             GhostLanternBlockEntity lanternBlockEntity = (GhostLanternBlockEntity)blockEntity;
    //             player.sendMessage(Text.literal(lanternBlockEntity.lanternID+" | "+lanternBlockEntity.level+" | "+lanternBlockEntity.xp), false);
    //         }

    //         return ActionResult.SUCCESS;
    //     }
        
    //     return ActionResult.PASS;
    // }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (state != null){
            NbtCompound nbt = BlockItem.getBlockEntityNbt(ctx.getStack());
            if (nbt == null){
                nbt = GhostLanternBlockEntity.getDefaultNbt();
            }

            int level = nbt.getInt(GhostLantern.LEVEL_TAG);

            return state.with(LEVEL, level);
        }
        return null;
    }

    // This one's weird (copied from ShulkerBoxBlock), 
    // the entity seems to have default values when accessed here.
    // Without it, middle clicking gives no EntityNbt tag at all (no tooltip),
    // Ctrl+Middle-Click works always tho 
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = super.getPickStack(world, pos, state);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GhostLanternBlockEntity){
            GhostLanternBlockEntity lanternBlockEntity = (GhostLanternBlockEntity)blockEntity;
            // GhostZ.log(lanternBlockEntity.lanternID+" | "+lanternBlockEntity.level+" | "+lanternBlockEntity.xp);
            lanternBlockEntity.setStackNbt(itemStack);
        }
        
        return itemStack;
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt == null){
            nbt = GhostLanternBlockEntity.getDefaultNbt();
            BlockItem.setBlockEntityNbt(stack, GRegistry.GHOST_LANTERN_BLOCK_ENTITY, nbt);
        }

        GhostLantern.addLevelTooltip(tooltip, nbt);

        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("tooltip.ghostz.can_be_placed").formatted(Formatting.ITALIC, Formatting.DARK_GRAY));
        tooltip.add(Text.literal("& ")
            .append(Text.translatable("tooltip.ghostz.does_not_gain_xp")).formatted(Formatting.ITALIC, Formatting.DARK_GRAY));
    }
}

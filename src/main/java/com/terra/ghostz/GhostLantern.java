package com.terra.ghostz;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GhostLantern extends BlockItem {
    @Nullable
    private String translationKey;

    public GhostLantern(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("item", Registries.ITEM.getId(this));
        }
        return this.translationKey;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtCompound defaultNBT = new NbtCompound();
        defaultNBT.putInt("level", 1);
        defaultNBT.putInt("xp", 0);
        defaultNBT.putInt("xpnext", GhostZ.CONFIG.levels.get(0).get("xpnext"));
        stack.setNbt(defaultNBT);
        return stack;
    }
    

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // return super.useOnBlock(context);
        if (context.getWorld().isClient()) {
            return ActionResult.PASS;
        }

        // PlayerEntity player = context.getPlayer();
        // World world = context.getWorld();

        // world.setBlockState(context.getBlockPos(), Wisp.WISP.getDefaultState());

        // context.getStack().damage(1, player.getRandom(), player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null);

        // return ActionResult.SUCCESS;

        ActionResult actionResult = this.place(new ItemPlacementContext(context));
        if (!actionResult.isAccepted() && this.isFood()) {
            ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
            return actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2;
        }
        return actionResult;
    }

    public ActionResult place(ItemPlacementContext context) {
        if (!this.getBlock().isEnabled(context.getWorld().getEnabledFeatures())) {
            return ActionResult.FAIL;
        }
        if (!context.canPlace()) {
            return ActionResult.FAIL;
        }
        ItemPlacementContext itemPlacementContext = this.getPlacementContext(context);
        if (itemPlacementContext == null) {
            return ActionResult.FAIL;
        }
        BlockState blockState = this.getPlacementState(itemPlacementContext);
        if (blockState == null) {
            return ActionResult.FAIL;
        }
        if (!this.place(itemPlacementContext, blockState)) {
            return ActionResult.FAIL;
        }
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        World world = itemPlacementContext.getWorld();
        PlayerEntity playerEntity = itemPlacementContext.getPlayer();
        ItemStack itemStack = itemPlacementContext.getStack();
        BlockState blockState2 = world.getBlockState(blockPos);
        if (blockState2.isOf(blockState.getBlock())) {
            blockState2 = this.placeFromNbt(blockPos, world, itemStack, blockState2);
            this.postPlacement(blockPos, world, playerEntity, itemStack, blockState2);
            blockState2.getBlock().onPlaced(world, blockPos, blockState2, playerEntity, itemStack);
            if (playerEntity instanceof ServerPlayerEntity) {
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
            }
        }
        BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
        world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
        world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            context.getStack().damage(1, playerEntity.getRandom(), playerEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity) playerEntity : null);
        }
        return ActionResult.success(world.isClient);
    }

    private BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound(BLOCK_STATE_TAG_KEY);
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();
            for (String string : nbtCompound2.getKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property == null) continue;
                String string2 = nbtCompound2.get(string).asString();
                blockState = GhostLantern.with(blockState, property, string2);
            }
        }
        if (blockState != state) {
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        }
        return blockState;
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String name) {
        return property.parse(name).map(value -> (BlockState)state.with(property, value)).orElse(state);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        NbtCompound nbt = stack.getNbt();
        tooltip.add(Text.of("Level "+nbt.getInt("level")));
        tooltip.add(Text.of("XP    "+nbt.getInt("xp")+"/"+nbt.getInt("xpnext")));
    }
}

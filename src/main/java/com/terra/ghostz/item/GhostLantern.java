package com.terra.ghostz.item;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.terra.ghostz.GhostZ;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class GhostLantern extends BlockItem {
    @Nullable
    private String translationKey;
    
    public static final String ID_TAG = "uuid";
    static final String LEVEL_TAG = "level";
    static final String XP_TAG = "xp";
    static final String WISP_POSITIONS_TAG = "wisps";
    static final int DEFAULT_XP_NEXT = GhostZ.CONFIG.getNextXP(1);
    public static final int MAX_LEVEL = GhostZ.CONFIG.getLevelCount();

    public GhostLantern(Block block, Item.Settings settings) {
        super(block, settings);
    }

    // ################################ METHODS FOR CUSTOM FUNCTIONALITY FIRST ############################ 

    public static void suckWisps(ItemStack lantern, World world){
        NbtList positions = getWispPositions(lantern);
        int positionCount = positions.size();
        if (positionCount == 0){
            return;
        }

        for (int i = 0; i < positionCount; i++){
            int[] nbt_pos = positions.getIntArray(i);
            BlockPos blockPos = new BlockPos(nbt_pos[0], nbt_pos[1], nbt_pos[2]);
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
        positions.clear();
    }

    public static void suckWisps(ItemStack lantern, World world, @Nullable PlayerEntity playerToMsg){
        NbtList positions = getWispPositions(lantern);
        int positionCount = positions.size();
        if (positionCount == 0){
            return;
        }

        for (int i = 0; i < positionCount; i++){
            int[] nbt_pos = positions.getIntArray(i);
            BlockPos blockPos = new BlockPos(nbt_pos[0], nbt_pos[1], nbt_pos[2]);
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
        }
        positions.clear();

        if (playerToMsg != null && playerToMsg.isPlayer()){
            playerToMsg.sendMessage(Text.translatable("message.ghostz.ghost_lantern_suck", positionCount).formatted(Formatting.GRAY), false);
        }
    }
    

    private static void removeOldestWisp(World world, NbtList positions){
        int[] nbt_pos = positions.getIntArray(0);
        positions.remove(0);
        BlockPos blockPos = new BlockPos(nbt_pos[0], nbt_pos[1], nbt_pos[2]);
        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
    }

    public static NbtCompound pingNBT(ItemStack lantern) {
        if (lantern.isEmpty()) {
            return new NbtCompound();
        }

        if (!lantern.hasNbt()) {
            lantern.setNbt(new NbtCompound());
        }

        NbtCompound nbt = lantern.getNbt();
        if (!nbt.contains(ID_TAG)) {
            nbt.putUuid(ID_TAG, UUID.randomUUID());
        }
        if (!nbt.contains(LEVEL_TAG)) {
            nbt.putInt(LEVEL_TAG, 1);
        }
        if (!nbt.contains(XP_TAG)) {
            nbt.putInt(XP_TAG, 0);
        }
        if (!nbt.contains(WISP_POSITIONS_TAG)) {
            nbt.put(WISP_POSITIONS_TAG, new NbtList());
        }

        return nbt;
    }

    public static boolean isLantern(ItemStack stack){
        if (stack.isEmpty() || !stack.hasNbt()) return false;
        NbtCompound nbt = stack.getNbt();
        return (nbt.contains(ID_TAG) && nbt.contains(LEVEL_TAG) && nbt.contains(XP_TAG) && nbt.contains(WISP_POSITIONS_TAG));
    }

    /**
     * Ping nbt and get positions
     * @param lantern
     * @return NbtList of wisp positions
     */
    public static NbtList getWispPositions(ItemStack lantern){
        return getWispPositions(pingNBT(lantern));
    }

    /**
     * Does not ping nbt
     * @param nbt
     * @return NbtList of wisp positions
     */
    public static NbtList getWispPositions(NbtCompound nbt){
        return nbt.getList(WISP_POSITIONS_TAG, NbtElement.INT_ARRAY_TYPE);
    }


    public static void onLevelChange(ItemStack lantern){
    }

    public static void onXpChange(ItemStack lantern){
        NbtCompound nbt = pingNBT(lantern);
        int level = nbt.getInt(LEVEL_TAG);
        if (level >= MAX_LEVEL){
            return;
        }

        int xp = nbt.getInt(XP_TAG);
        int maxXP = GhostZ.CONFIG.levels.get(level).get("xpnext");

        if (xp >= maxXP){
            level += 1;
            nbt.putInt(LEVEL_TAG, level);
            nbt.putInt(XP_TAG, xp-maxXP);
            onLevelChange(lantern);
        }
        
        printLantern(lantern);
    }

    public static void printLantern(ItemStack lantern){
        NbtCompound nbt = pingNBT(lantern);

        GhostZ.log("=============== PRINT LANTERN NBT =============\n"+nbt);

        Entity h = lantern.getHolder();
        if (h != null && h.isPlayer()){
            PlayerEntity player = (PlayerEntity)h;
            player.sendMessage(Text.literal("Print Lantern NBT: "+nbt).formatted(Formatting.GRAY));
        }
    }

    public static void addPosToNbt(ItemStack lantern, BlockPos pos){
        getWispPositions(lantern).add(new NbtIntArray(new int[] { pos.getX(), pos.getY(), pos.getZ() }));
    }

    public static void removePosFromNbt(ItemStack lantern, BlockPos pos){
        var positions = getWispPositions(lantern);
        GhostZ.log("Removing "+pos+" from "+positions);
        positions.remove(new NbtIntArray(new int[] { pos.getX(), pos.getY(), pos.getZ() }));

        printLantern(lantern);
    }

    // ########################################### OVERRIDES ######################################

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack lantern = player.getStackInHand(hand);
        
        if (!world.isClient && player.isSneaking()){
            suckWisps(lantern, world, player);
            return new TypedActionResult<>(ActionResult.CONSUME, lantern);
        }

        return super.use(world, player, hand);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer().isSneaking()) {
            return ActionResult.PASS;
        }

        NbtCompound nbt = pingNBT(context.getStack());
        NbtList positions = getWispPositions(nbt);
        int wispCount = positions.size();
        if (wispCount >= GhostZ.CONFIG.getWispCount(nbt.getInt(LEVEL_TAG))){
            removeOldestWisp(context.getWorld(), positions);;
        }

        return super.useOnBlock(context);
    }

    @Override
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
        // THIS ACTUALLY PLACES THE BLOCK
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
                Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) playerEntity, blockPos, itemStack);
            }
        }
        BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
        world.playSound(playerEntity, blockPos, this.getPlaceSound(blockState2), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f,
                blockSoundGroup.getPitch() * 0.8f);
        world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(playerEntity, blockState2));
        
        // Removed stack decrement

        return ActionResult.success(world.isClient);
    }

    // Copied from BlockItem (it is private) for place method to work
    private BlockState placeFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state) {
        BlockState blockState = state;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            NbtCompound nbtCompound2 = nbtCompound.getCompound(BLOCK_STATE_TAG_KEY);
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();
            for (String string : nbtCompound2.getKeys()) {
                Property<?> property = stateManager.getProperty(string);
                if (property == null)
                    continue;
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
        return property.parse(name).map(value -> (BlockState) state.with(property, value)).orElse(state);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = pingNBT(stack);
        int level = nbt.getInt(LEVEL_TAG);
        int wispCount = getWispPositions(nbt).size();
        // printLantern(stack);


        tooltip.add(Text.literal("Level ").formatted(Formatting.GRAY).append(Text.literal("" + level).formatted(Formatting.DARK_AQUA))
                .append(Text.literal("/").formatted(Formatting.GRAY)).append(Text.literal("" + MAX_LEVEL).formatted(Formatting.DARK_AQUA)));

        if (level < MAX_LEVEL) {
            tooltip.add(Text.literal("XP ").formatted(Formatting.GRAY).append(Text.literal("" + nbt.getInt(XP_TAG)).formatted(Formatting.DARK_AQUA))
                    .append(Text.literal("/").formatted(Formatting.GRAY))
                    .append(Text.literal("" + GhostZ.CONFIG.getNextXP(level)).formatted(Formatting.DARK_AQUA)));
        } else {
            tooltip.add(Text.literal("Max Level").formatted(Formatting.DARK_AQUA));
        }
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Wisps ").formatted(Formatting.GRAY).append(Text.literal("" + wispCount).formatted(Formatting.DARK_AQUA))
                .append(Text.literal("/").formatted(Formatting.GRAY)).append(Text.literal("" + GhostZ.CONFIG.getWispCount(level)).formatted(Formatting.DARK_AQUA)));
    }


    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getWispPositions(stack).size() > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        NbtCompound nbt = pingNBT(stack);
        int level = nbt.getInt(LEVEL_TAG);
        int wispCount = getWispPositions(stack).size();

        return Math.round(13f - (float)wispCount * 13f / (float)GhostZ.CONFIG.getWispCount(level));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound nbt = pingNBT(stack);
        int level = nbt.getInt(LEVEL_TAG);
        int wispCount = getWispPositions(stack).size();
        int maxWispCount = GhostZ.CONFIG.getWispCount(level);

        float f = Math.max(0.0f, ((float)maxWispCount - (float)wispCount) / (float)maxWispCount);
        return MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f);
    }


    // Does nothing ..?
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        pingNBT(stack);
        return stack;
    }


    @Override
    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("item", Registries.ITEM.getId(this));
        }
        return this.translationKey;
    }
}

package com.terra.ghostz.entity;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.terra.ghostz.item.GhostLantern;
import com.terra.ghostz.util.GRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

public class GhostLanternBlockEntity extends BlockEntity implements Nameable{
    @Nullable
    private Text customName;
    
    public UUID lanternID;
    public int level = 1;
    public int xp;

    public GhostLanternBlockEntity(BlockPos pos, BlockState state) {
        super(GRegistry.GHOST_LANTERN_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (lanternID != null){
            nbt.putUuid(GhostLantern.ID_TAG, lanternID);
        }
        nbt.putInt(GhostLantern.LEVEL_TAG, level);
        nbt.putInt(GhostLantern.XP_TAG, xp);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.containsUuid(GhostLantern.ID_TAG)){
            lanternID = nbt.getUuid(GhostLantern.ID_TAG);
        } else {
            lanternID = UUID.randomUUID();
        }
        level = nbt.getInt(GhostLantern.LEVEL_TAG);
        xp = nbt.getInt(GhostLantern.XP_TAG);
    }


    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return Text.translatable("block.ghostz.ghost_lantern_block");
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(Text name){
        this.customName = name;
    }
}

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
        nbt.putUuid(GhostLantern.ID_TAG, lanternID != null ? lanternID : UUID.randomUUID());
        nbt.putInt(GhostLantern.LEVEL_TAG, level);
        nbt.putInt(GhostLantern.XP_TAG, xp);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        lanternID = nbt.containsUuid(GhostLantern.ID_TAG) ? nbt.getUuid(GhostLantern.ID_TAG) : UUID.randomUUID();
        level = nbt.getInt(GhostLantern.LEVEL_TAG);
        xp = nbt.getInt(GhostLantern.XP_TAG);
    }


    public static NbtCompound getDefaultNbt(){
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid(GhostLantern.ID_TAG, UUID.randomUUID());
        nbt.putInt(GhostLantern.LEVEL_TAG, 1);
        nbt.putInt(GhostLantern.XP_TAG, 0);

        return nbt;
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

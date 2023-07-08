package com.terra.ghostz.entity;

import java.util.UUID;

import com.terra.ghostz.util.GRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class WispEntity extends BlockEntity {
    public UUID playerID;
    public UUID lanternID;

    public WispEntity(BlockPos pos, BlockState state) {
        super(GRegistry.WISP_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (playerID != null){
            nbt.putUuid("playerID", playerID);
        }
        if (lanternID != null){
            nbt.putUuid("lanternID", lanternID);
        }

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        playerID = nbt.containsUuid("playerID") ? nbt.getUuid("playerID") : null;
        lanternID = nbt.containsUuid("lanternID") ? nbt.getUuid("lanternID") : null;
    }
}

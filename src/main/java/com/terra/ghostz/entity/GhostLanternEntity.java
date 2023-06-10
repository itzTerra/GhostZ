package com.terra.ghostz.entity;

import com.terra.ghostz.util.GRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class GhostLanternEntity extends BlockEntity {
    private int number = 7;

    public GhostLanternEntity(BlockPos pos, BlockState state) {
        super(GRegistry.GHOST_LANTERN_ENTITY, pos, state);
    }

    // Serialize the BlockEntity
    @Override
    public void writeNbt(NbtCompound nbt) {
        // Save the current value of the number to the nbt
        nbt.putInt("number", number);

        super.writeNbt(nbt);
    }

    // Deserialize the BlockEntity
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        number = nbt.getInt("number");
    }
}

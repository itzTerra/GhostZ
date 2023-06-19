package com.terra.ghostz.util;

import org.jetbrains.annotations.Nullable;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.item.GhostLantern;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Debugging {
    public static void printLantern(ItemStack lantern){
        NbtCompound nbt = GhostLantern.pingNBT(lantern);
        GhostZ.log("=============== LANTERN NBT =============\n"+nbt);
    }

    public static void printWisp(ItemStack stack, BlockState state, @Nullable PlayerEntity player){
        if (player != null && player.isPlayer()){
            player.sendMessage(Text.literal("Blockstate: " + state.toString()).formatted(Formatting.GRAY));
        } else{
            GhostZ.log("=============== WISP INFO ==================\nBlockstate: " + state.toString());
        }
    }
}

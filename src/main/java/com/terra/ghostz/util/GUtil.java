package com.terra.ghostz.util;

import com.terra.ghostz.GhostLantern;

import net.minecraft.item.ItemStack;

public class GUtil {
    public static boolean isGhostLantern(ItemStack stack){
        return (stack.getItem() instanceof GhostLantern);
    }
}

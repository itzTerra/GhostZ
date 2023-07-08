package com.terra.ghostz.mixin;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.item.GhostLantern;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    // Sucks wisps on /clear command
    @Inject(method = "remove", at = @At(value = "HEAD"))
    private void onRemove(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory, CallbackInfoReturnable<Integer> cir){
        PlayerInventory playerInventory = (PlayerInventory)(Object)this;

        ArrayList<ItemStack> lanterns = GhostLantern.lanternsInInventory(playerInventory);
        for (ItemStack lantern : lanterns) {
            if (shouldRemove.test(lantern)){
                GhostLantern.suckWisps(lantern, playerInventory.player.getWorld());
            }
        }
    }
}
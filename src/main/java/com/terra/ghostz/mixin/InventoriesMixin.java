package com.terra.ghostz.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.event.ItemRemovedCallback;
import com.terra.ghostz.item.GhostLantern;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;

@Mixin(Inventories.class)
public class InventoriesMixin {
    @Inject(at = @At("HEAD"), method = "splitStack")
	private static void onSplit(List<ItemStack> stacks, int slot, int amount, CallbackInfoReturnable<ItemStack> info) {
		ItemRemovedCallback.EVENT.invoker().action("Inventories.splitStack");
    }

    @Inject(at = @At("HEAD"), method = "removeStack")
	private static void onRemoveStack(List<ItemStack> stacks, int slot, CallbackInfoReturnable<ItemStack> info) {
		ItemRemovedCallback.EVENT.invoker().action("Inventories.removeStack");
        for (ItemStack itemStack : stacks) {
            if (GhostLantern.isLantern(itemStack)){
                ItemRemovedCallback.EVENT.invoker().action("LANTERN >> Inventories.removeStack");
            }
        }
    }
}

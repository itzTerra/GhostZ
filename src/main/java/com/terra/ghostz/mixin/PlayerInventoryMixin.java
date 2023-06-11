package com.terra.ghostz.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.event.ItemDroppedCallback;
import com.terra.ghostz.event.ItemRemovedCallback;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Inject(at = @At("HEAD"), method = "remove")
	private void onRemove(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory, CallbackInfoReturnable<ItemStack> info) {
		ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.remove");
    }

    @Inject(at = @At("HEAD"), method = "removeStack(I)Lnet/minecraft/item/ItemStack;")
	private void onRemoveStack(int slot, CallbackInfoReturnable<ItemStack> info) {
		ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.removeStack");
    }

    // @Inject(at = @At("HEAD"), method = "setStack")
	// private void onSetStack(int slot, ItemStack stack, CallbackInfo info) {
	// 	ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.setStack");
    // }
}

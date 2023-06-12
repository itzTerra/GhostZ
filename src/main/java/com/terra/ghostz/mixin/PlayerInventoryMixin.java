package com.terra.ghostz.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.event.ItemDroppedCallback;
import com.terra.ghostz.event.ItemRemovedCallback;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    // @Inject(at = @At("HEAD"), method = "remove")
	// private void onRemove(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory, CallbackInfoReturnable<ItemStack> info) {
	// 	ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.remove");
    // }

    @Inject(at = @At("RETURN"), method = "removeStack(II)Lnet/minecraft/item/ItemStack;")
	private void onRemoveStack(int slot, int amount, CallbackInfoReturnable<ItemStack> info) {
		ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.removeStack", info.getReturnValue(), ((PlayerInventory)(Object)this).player);
    }

    // @Inject(at = @At("HEAD"), method = "markDirty")
	// private void odMarkDirty(CallbackInfo info) {
	// 	ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.markDirty");
    // }

    // @Inject(at = @At("HEAD"), method = "setStack")
	// private void onSetStack(int slot, ItemStack stack, CallbackInfo info) {
    //     if (stack.isEmpty()){
	// 	    ItemRemovedCallback.EVENT.invoker().action("PlayerInventory.setStack (empty)");
    //     }
    // }
}

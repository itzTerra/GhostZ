package com.terra.ghostz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.event.ItemRemovedCallback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin {
    // @Inject(method = "quickMove", at = @At("HEAD"))
    // private void onShiftClick(PlayerEntity player, int slotIndex, CallbackInfoReturnable<ItemStack> info) {
    //     Slot slot = ((PlayerScreenHandler)(Object)this).getSlot(slotIndex);
    //     if (slot.hasStack()) {
    //         ItemStack itemStack = slot.getStack();
    //         ItemRemovedCallback.EVENT.invoker().action("PlayerScreenHandler.quickMove", itemStack, player);
    //     }
    // }
}

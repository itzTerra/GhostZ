package com.terra.ghostz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.GhostZ;
import com.terra.ghostz.event.ItemRemovedCallback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(at = @At("HEAD"), method = "insertItem")
    private void onShiftClick(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> info) {
        GhostZ.log(""+stack.getHolder());
        ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.insertItem", stack, (PlayerEntity)stack.getHolder());
    }

    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
        ))
    private void onSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        GhostZ.log(""+slotIndex);
        ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.swap", player.getInventory().getStack(button), player);
    }
}

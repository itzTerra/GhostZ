package com.terra.ghostz.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terra.ghostz.event.LanternRemovedCallback;
import com.terra.ghostz.item.GhostLantern;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {  
    @Redirect(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"
        ))
    private ItemStack redirectShiftClick(ScreenHandler screenHandler, PlayerEntity player, int slotIndex) {
        ItemStack stack = screenHandler.quickMove(player, slotIndex);
        
        if (stack.getItem() instanceof GhostLantern && !player.getInventory().contains(stack)){
            UUID lanternID = GhostLantern.pingNBT(stack).getUuid(GhostLantern.ID_TAG);
            var stacks = ((ScreenHandler)(Object)this).getStacks();
            for (ItemStack itemStack : stacks) {
                if (itemStack.getItem() instanceof GhostLantern && GhostLantern.pingNBT(itemStack).getUuid(GhostLantern.ID_TAG).equals(lanternID)){
                    LanternRemovedCallback.EVENT.invoker().action("ScreenHandler.quickMove", itemStack, player);
                }
            }
        }
        return stack;
    }
    
    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
        ))
    private void onHotbarSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
        PlayerInventory playerInventory = player.getInventory();
        ItemStack stack = playerInventory.getStack(button);
        if (stack.getItem() instanceof GhostLantern && !slot.inventory.equals(playerInventory)){
            LanternRemovedCallback.EVENT.invoker().action("ScreenHandler.swap", stack, player);
        }
    }

    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V",
            ordinal = 2
        ))
    private void onClickoutBlank(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
        ItemStack stack = slot.getStack();
        if (stack.getItem() instanceof GhostLantern && !slot.inventory.equals(player.getInventory())){
            LanternRemovedCallback.EVENT.invoker().action("ScreenHandler.clickoutBlank", stack, player);
        }
    }

    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V",
            ordinal = 4
        ))
    private void onClickoutSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
        ItemStack stack = ((ScreenHandler)(Object)this).getCursorStack();
        if (stack.getItem() instanceof GhostLantern && !slot.inventory.equals(player.getInventory())){
            LanternRemovedCallback.EVENT.invoker().action("ScreenHandler.clickoutSwap", stack, player);
        }
    }


    // ####################################################### PROMISING STUFF ############################################

    // @Inject(method = "insertItem", at = @At("HEAD"))
    // private void onShiftClick(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> info) {
    //     GhostZ.log(""+stack.getHolder());
    //     ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.insertItem", stack, (PlayerEntity)stack.getHolder());
    // }

    // @Inject(method = "quickMove", at = @At("HEAD"))
    // private void onShiftClick(PlayerEntity player, int slotIndex, CallbackInfoReturnable<ItemStack> info) {
    //     Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
    //     if (slot.hasStack()) {
    //         ItemStack itemStack = slot.getStack();
    //         ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.quickMove", itemStack, player);
    //     }
    // }

    // @Inject(
    //     method = "internalOnSlotClick",
    //     at = @At(
    //         value = "INVOKE",
    //         target = "Lnet/minecraft/screen/ScreenHandler;quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"
    //     ))
    // private void onShiftClick2(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
    //     Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
    //     PlayerInventory playerInventory = player.getInventory();
    //     if (!slot.inventory.equals(playerInventory)){
    //         ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.clickQuickMove", playerInventory.getStack(button), player);
    //     }
    // }

    // @Redirect(
    //     method = "insertItem", 
    //     at = @At(
    //         value = "INVOKE",
    //         target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V",
    //         ordinal = 0
    //     ))
    // private void onShiftClick1(Slot slot, ItemStack stack) {
    //     PlayerEntity player = (PlayerEntity)stack.getHolder();
    //     slot.setStack(stack.split(slot.getMaxItemCount()));
    //     stack = slot.getStack();
    //     ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.insertItem", stack, player);
    // }

    // @Redirect(
    //     method = "insertItem", 
    //     at = @At(
    //         value = "INVOKE",
    //         target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V",
    //         ordinal = 1
    //     ))
    // private void onShiftClick2(Slot slot, ItemStack stack) {
    //     PlayerEntity player = (PlayerEntity)stack.getHolder();
    //     slot.setStack(stack.split(stack.getCount()));
    //     stack = slot.getStack();
    //     ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.insertItem", stack, player);
    // }
}

package com.terra.ghostz.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terra.ghostz.item.GhostLantern;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {  
    // Shift-click to chest
    @Redirect(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;"
        ))
    private ItemStack redirectShiftClick(ScreenHandler screenHandler, PlayerEntity player, int slotIndex) {
        // Why is this creative stuff client-side?
        // if (player.isCreative()){
        //     Slot slot2;
        //     if (slotIndex >= screenHandler.slots.size() - 9 && slotIndex < screenHandler.slots.size() && (slot2 = (Slot)screenHandler.slots.get(slotIndex)) != null && slot2.hasStack()) {
        //         ItemStack oldStack = slot2.getStack();
        //         if (GhostLantern.isLantern(oldStack)){
        //             GhostLantern.suckWisps(oldStack, player.getWorld(), player);
        //         }
        //     }
        // }

        ItemStack movedStack = screenHandler.quickMove(player, slotIndex);

        if (player == null || 
            player.getWorld().isClient() || 
            !GhostLantern.isLantern(movedStack) || 
            player.getInventory().contains(movedStack))
        {
            return movedStack;
        }
        
        UUID lanternID = GhostLantern.pingNbt(movedStack).getUuid(GhostLantern.ID_TAG);
        DefaultedList<ItemStack> inventoryStacks = screenHandler.getStacks();
        for (ItemStack stack : inventoryStacks) {
            if (GhostLantern.isLantern(stack) && GhostLantern.pingNbt(stack).getUuid(GhostLantern.ID_TAG).equals(lanternID)){
                GhostLantern.suckWisps(stack, player.getWorld(), player);
            }
        }
        return movedStack;
    }
    
    // Swap with numbers
    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
        ))
    private void onHotbarSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (player == null || player.getWorld().isClient()){
            return;
        }

        Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
        PlayerInventory playerInventory = player.getInventory();
        ItemStack stack = playerInventory.getStack(button);

        if (!GhostLantern.isLantern(stack) || slot.inventory.equals(playerInventory)){
            return;
        }

        GhostLantern.suckWisps(stack, player.getWorld(), player);
    }

    // Manual place to blank chest slot
    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V",
            ordinal = 2
        ))
    private void onClickoutBlank(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (player == null || player.getWorld().isClient()){
            return;
        }

        Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
        ItemStack stack = slot.getStack();

        if (!GhostLantern.isLantern(stack) || slot.inventory.equals(player.getInventory())){
            return;
        }

        GhostLantern.suckWisps(stack, player.getWorld(), player);
    }

    // Manual place to occupied chest slot
    @Inject(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V",
            ordinal = 4
        ))
    private void onClickoutSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (player == null || player.getWorld().isClient()){
            return;
        }

        Slot slot = ((ScreenHandler)(Object)this).getSlot(slotIndex);
        ItemStack stack = ((ScreenHandler)(Object)this).getCursorStack();

        if (!GhostLantern.isLantern(stack) || slot.inventory.equals(player.getInventory())){
            return;
        }

        GhostLantern.suckWisps(stack, player.getWorld(), player);
    }

    // Creative Middle-mouse clone
    @Redirect(
        method = "internalOnSlotClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V",
            ordinal = 5
        ))
    private void onClone(ScreenHandler screenHandler, ItemStack stack) {
        if (GhostLantern.isLantern(stack)){
            NbtCompound nbt = stack.getNbt();
            nbt.putUuid(GhostLantern.ID_TAG, UUID.randomUUID());
            GhostLantern.getWispPositions(nbt).clear();
        }
        screenHandler.setCursorStack(stack);
    }


    // ####################################################### PROMISING STUFF ############################################

    // @Inject(method = "insertItem", at = @At("HEAD"))
    // private void onShiftClick(ItemStack stack, int startIndex, int endIndex, boolean fromLast, CallbackInfoReturnable<Boolean> cir) {
    //     GhostZ.log(""+stack.getHolder());
    //     ItemRemovedCallback.EVENT.invoker().action("ScreenHandler.insertItem", stack, (PlayerEntity)stack.getHolder());
    // }

    // @Inject(method = "quickMove", at = @At("HEAD"))
    // private void onShiftClick(PlayerEntity player, int slotIndex, CallbackInfoReturnable<ItemStack> ci) {
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
    // private void onShiftClick2(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
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

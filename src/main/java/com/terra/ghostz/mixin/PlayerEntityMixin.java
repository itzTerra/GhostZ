package com.terra.ghostz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.item.GhostLantern;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
	private void beforeStackDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        
        if (player == null || player.getWorld().isClient()){
            return;
        }

        if (GhostLantern.isLantern(stack)){
            GhostLantern.suckWisps(stack, player.getWorld(), player);
        }
    }
}
package com.terra.ghostz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terra.ghostz.item.GhostLantern;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {

    @Inject(method = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V", at = @At(value="HEAD"))
    private void onBeforeSet(ItemStack value, boolean update, CallbackInfo ci){
        if (GhostLantern.isLantern(value)){
            GhostLantern.suckWisps(value, ((ItemFrameEntity)(Object)this).getWorld());
        }
    }
}

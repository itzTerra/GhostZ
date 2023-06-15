package com.terra.ghostz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.terra.ghostz.item.GhostLantern;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    private void ghostzOnDeath(DamageSource source, CallbackInfo ci) {
        GhostLantern.onLivingDeath(source, ((LivingEntity) (Object) this));
    }
}
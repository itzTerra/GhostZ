package com.terra.ghostz.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.terra.ghostz.block.GhostLanternBlock;
import com.terra.ghostz.item.GhostLantern;
import com.terra.ghostz.util.GRegistry;

import net.minecraft.block.Block;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.registry.DynamicRegistryManager;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin {
    @Shadow
    public abstract ItemStack getOutput(DynamicRegistryManager registryManager);

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private void onCraft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager, CallbackInfoReturnable<ItemStack> cir) {
        List<ItemStack> inputStacks = recipeInputInventory.getInputStacks();
        for (ItemStack stack : inputStacks) {
            if (stack.getItem() instanceof GhostLantern){
                ItemStack placeableLantern = this.getOutput(dynamicRegistryManager).copy();
                NbtCompound lanternNbt = GhostLantern.pingNbt(stack, true);

                NbtCompound placeableBlockEntityNbt = new NbtCompound();
                placeableBlockEntityNbt.putUuid(GhostLantern.ID_TAG, lanternNbt.getUuid(GhostLantern.ID_TAG));
                placeableBlockEntityNbt.putInt(GhostLantern.LEVEL_TAG, lanternNbt.getInt(GhostLantern.LEVEL_TAG));
                placeableBlockEntityNbt.putInt(GhostLantern.XP_TAG, lanternNbt.getInt(GhostLantern.XP_TAG));
                BlockItem.setBlockEntityNbt(placeableLantern, GRegistry.GHOST_LANTERN_BLOCK_ENTITY, placeableBlockEntityNbt);

                if (stack.hasCustomName()) {
                    placeableLantern.setCustomName(stack.getName());
                }

                cir.setReturnValue(placeableLantern);
            } else if (Block.getBlockFromItem(stack.getItem()) instanceof GhostLanternBlock){
                ItemStack lantern = this.getOutput(dynamicRegistryManager).copy();
                NbtCompound placeableNbt = BlockItem.getBlockEntityNbt(stack);
                
                NbtCompound lanternNbt = lantern.getOrCreateNbt();
                if (placeableNbt.containsUuid(GhostLantern.ID_TAG)){
                    lanternNbt.putUuid(GhostLantern.ID_TAG, placeableNbt.getUuid(GhostLantern.ID_TAG));
                }
                lanternNbt.putInt(GhostLantern.LEVEL_TAG, placeableNbt.getInt(GhostLantern.LEVEL_TAG));
                lanternNbt.putInt(GhostLantern.XP_TAG, placeableNbt.getInt(GhostLantern.XP_TAG));
                GhostLantern.pingNbt(lantern);
                
                if (stack.hasCustomName()) {
                    lantern.setCustomName(stack.getName());
                }

                cir.setReturnValue(lantern);
            }
        }
    }
}

package com.terra.ghostz;

import com.terra.ghostz.util.GRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.EndRodParticle;

public class GhostZClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(GRegistry.WISP_GLITTER, EndRodParticle.Factory::new);
    }
}
package com.terra.ghostz;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terra.ghostz.config.GConfig;
import com.terra.ghostz.event.GEvents;
import com.terra.ghostz.util.GRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class GhostZ implements ModInitializer {
    public static final String MOD_ID = "ghostz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String CONFIG_PATH = FabricLoader.getInstance().getConfigDir() + "/ghostz.json";
	public static GConfig CONFIG;

    // public static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");
    // not implementing it rn

    @Override
    public void onInitialize() {
        LOGGER.info("GhostZ initialization");

		CONFIG = GConfig.loadConfig(new File(CONFIG_PATH));

        GRegistry.register();

        GEvents.register();
    }

    public static void log(String... messages){
        LOGGER.info("(?) "+String.join(" ", messages));
    }

    public static void log(boolean useSep, String sep, String... messages){
        if (useSep){
            LOGGER.info("(?) "+String.join(sep, messages));
        } else{
            LOGGER.info("(?) "+sep+" "+String.join(" ", messages));
        }
    }

    public static void warn(String message){
        LOGGER.warn("(!) "+message);
    }

    public static void error(String message){
        LOGGER.error("(!!!) "+message);
    }
}
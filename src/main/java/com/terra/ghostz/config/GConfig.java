package com.terra.ghostz.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terra.ghostz.GhostZ;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int initialXpReq = 100;
    public float XpMultiplier = 1.5f;

    public ArrayList<Map<String, Integer>> levels = new ArrayList<>(){{
        add(new LinkedHashMap<>() {{
            put("luminance", 10);
            put("wisps", 5);
        }});
        add(new LinkedHashMap<>() {{
            put("luminance", 12);
            put("wisps", 10);
        }});
        add(new LinkedHashMap<>() {{
            put("luminance", 14);
            put("wisps", 15);
        }});
        add(new LinkedHashMap<>() {{
            put("luminance", 16);
            put("wisps", 20);
        }});
    }};

    public int getMaxWisps(){
        int res = 1;

        for (Map<String,Integer> level : levels) {
            if (level.get("wisps") > res){
                res = level.get("wisps");
            }
        }

        return res;
    }

    public boolean isValid(){
        if (levels.size() == 0){return false;}

        for (Map<String,Integer> level : levels) {
            if (level.get("luminance") < 0 || level.get("wisps") < 1){
                return false;
            }
        }

        return true;
    }


    public static GConfig loadConfig(File file) {
        GConfig config;

        if (file.exists() && file.isFile()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ) {
                config = GSON.fromJson(bufferedReader, GConfig.class);

                if (!config.isValid()){
                    GhostZ.LOGGER.error("Invalid config, falling back to default...");
                    config = new GConfig();
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        } else {
            config = new GConfig();
            config.saveConfig(file);
        }

        return config;
    }

    public void saveConfig(File config) {
        try (
                FileOutputStream stream = new FileOutputStream(config);
                Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        ) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }
}

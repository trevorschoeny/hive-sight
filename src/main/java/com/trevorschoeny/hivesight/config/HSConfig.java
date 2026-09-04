package com.trevorschoeny.hivesight.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import com.trevorschoeny.hivesight.HiveSight;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hive Sight config: one toggle per feature, all default on, no master
 * switch. Same static-field + JSON shape as the other house mods; persisted
 * to {@code config/hivesight/config.json}.
 *
 * <p>Where each toggle is read matters on a dedicated server: HUD is client
 * only; Eject is read on both sides, each from its own file, so the server's
 * file decides whether ejects happen there.
 */
public final class HSConfig {

    private HSConfig() {}

    private static final int CURRENT_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Shift-look card with bee and honey counts. Client-side.
    private static boolean hud = true;
    // Shift-right-click lets one bee out. Read on both sides.
    private static boolean eject = true;
    // Honey drips on all four sides of a full hive. Client-side (a resource pack).
    private static boolean honeyFaces = true;

    private static boolean loaded = false;

    private static Path filePath() {
        return FabricLoader.getInstance().getConfigDir()
                .resolve("hivesight")
                .resolve("config.json");
    }

    public static void load() {
        if (loaded) return;
        loaded = true;
        Path path = filePath();
        if (!Files.exists(path)) {
            HiveSight.LOGGER.info("[config] no config at {}, using defaults", path);
            return;
        }
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            hud = readBool(root, "hud", hud);
            eject = readBool(root, "eject", eject);
            honeyFaces = readBool(root, "honeyFaces", honeyFaces);
            HiveSight.LOGGER.info("[config] loaded from {}", path);
        } catch (IOException | JsonSyntaxException | IllegalStateException e) {
            HiveSight.LOGGER.error("[config] failed to read {}, using defaults", path, e);
        }
    }

    private static boolean readBool(JsonObject root, String key, boolean fallback) {
        return root.has(key) && root.get(key).isJsonPrimitive()
                ? root.get(key).getAsBoolean() : fallback;
    }

    private static void save() {
        Path path = filePath();
        try {
            Files.createDirectories(path.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("version", CURRENT_VERSION);
            root.addProperty("hud", hud);
            root.addProperty("eject", eject);
            root.addProperty("honeyFaces", honeyFaces);
            Files.writeString(path, GSON.toJson(root));
        } catch (IOException e) {
            HiveSight.LOGGER.error("[config] failed to write {}, changes won't persist", path, e);
        }
    }

    public static boolean hud() { return hud; }
    public static void setHud(boolean v) { hud = v; save(); }

    public static boolean eject() { return eject; }
    public static void setEject(boolean v) { eject = v; save(); }

    public static boolean honeyFaces() { return honeyFaces; }
    public static void setHoneyFaces(boolean v) { honeyFaces = v; save(); }
}

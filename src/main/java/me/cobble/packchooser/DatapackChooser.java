package me.cobble.packchooser;

import com.google.gson.JsonObject;
import me.cobble.packchooser.utils.PackManifests;
import net.fabricmc.api.ModInitializer;

public class DatapackChooser implements ModInitializer {

    private static JsonObject manifest;
    private static final String MOD_ID = "packchooser";

    @Override
    public void onInitialize() {
        manifest = PackManifests.getManifests();
    }

    public static JsonObject getManifest() {
        return manifest;
    }

    public static String getModId() {
        return MOD_ID;
    }
}

package me.cobble.datapackchooser;

import com.google.gson.JsonObject;
import me.cobble.datapackchooser.utils.PackManifests;
import net.fabricmc.api.ModInitializer;

public class DatapackChooser implements ModInitializer {

    private static JsonObject manifest;

    @Override
    public void onInitialize() {
        manifest = PackManifests.fetchPacks();
    }

    public static JsonObject getManifest() {
        return manifest;
    }
}

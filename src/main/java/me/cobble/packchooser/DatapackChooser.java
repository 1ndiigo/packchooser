package me.cobble.packchooser;

import com.google.gson.JsonObject;
import me.cobble.packchooser.utils.PackManifests;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatapackChooser implements ModInitializer {

    private static JsonObject manifest;
    private static final String MOD_ID = "packchooser";
    private static final Logger LOGGER = LoggerFactory.getLogger(getModId());

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

    public static Logger getLogger() { return LOGGER; }
}

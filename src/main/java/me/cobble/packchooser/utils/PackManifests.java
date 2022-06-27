package me.cobble.packchooser.utils;

import com.google.gson.*;
import me.cobble.packchooser.DatapackChooser;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

public class PackManifests {

    // TODO: Clean all the code up

    private static File manifestFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + DatapackChooser.getModId() + "/manifest.json");
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setLenient().create();
    private static final Logger logger = DatapackChooser.getLogger();

    /**
     * Gets manifest
     *
     * @return manifest as Json
     */
    public static JsonObject getManifests() {

        if (!manifestFile.exists()) {
            fetchManifest();
        }

        try {
            JsonObject readManifest = gson.fromJson(new FileReader(manifestFile), JsonObject.class);
            long timeGenerated = readManifest.get("time_downloaded").getAsLong();
            if (Duration.between(Instant.ofEpochMilli(timeGenerated), Instant.now().atOffset(ZoneOffset.UTC)).toDays() > 30) {
                manifestFile.delete();
                fetchManifest();
            }
            return readManifest;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JsonObject getByName(String name) {
        for (JsonElement object : DatapackChooser.getManifest().get("packs").getAsJsonArray()) {
            if (object.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(name)) {
                return object.getAsJsonObject();
            }
        }
        return new JsonObject();
    }

    public static JsonArray searchSimilar(String searchString) {

        if (searchString.equalsIgnoreCase("")) return getManifests().get("packs").getAsJsonArray();

        String lowerCaseString = searchString.toLowerCase();
        JsonArray array = new JsonArray();
        for (JsonElement jsonElement : getManifests().get("packs").getAsJsonArray()) {
            if (searchString.equalsIgnoreCase(jsonElement.getAsJsonObject().get("name").getAsString())) {
                array.add(getByName(lowerCaseString));
                return array;
            }
        }

        for (JsonElement jsonObject : getManifests().get("packs").getAsJsonArray()) {
            if (jsonObject.getAsJsonObject().get("name").getAsString().toLowerCase().contains(lowerCaseString)) {
                array.add(jsonObject);
            }
        }
        return array;
    }

    public static void fetchManifest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://raw.githubusercontent.com/1ndiigo/packchooser-manifests/main/mainfest.json"))
                .build();

        try {
            manifestFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + DatapackChooser.getModId());
            manifestFile.mkdirs();
            manifestFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + DatapackChooser.getModId() + "/manifest.json");
            manifestFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAcceptAsync(body -> {
            logger.info("Fetching manifests...");

            JsonObject object = gson.fromJson(body, JsonObject.class);
            object.addProperty("time_downloaded", Instant.now(Clock.systemUTC()).toEpochMilli());
            try {
                FileWriter writer = new FileWriter(manifestFile);
                writer.write(gson.toJson(object));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).join();
        logger.info("Manifests fetched!");
    }
}

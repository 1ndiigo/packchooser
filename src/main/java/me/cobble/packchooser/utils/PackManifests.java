package me.cobble.packchooser.utils;

import com.google.gson.*;
import me.cobble.packchooser.DatapackChooser;
import net.fabricmc.loader.api.FabricLoader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class PackManifests {

    private static File manifestFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + DatapackChooser.getModId() + "/manifest.json");
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().setLenient().create();

    /**
     * Gets manifest
     *
     * @return manifest as Json
     */
    public static JsonObject getManifests() {

        if (!manifestFile.exists()) {
            fetchManifest();
        } else {

            try {
                JsonObject generatedManifestFile = gson.fromJson(new FileReader(manifestFile), JsonObject.class);
                long timeGenerated = generatedManifestFile.get("timeDownloaded").getAsLong();
                if (Duration.between(Instant.ofEpochMilli(timeGenerated), Instant.now()).toDays() > 30) {
                    manifestFile.delete();
                    fetchManifest();
                }
                return generatedManifestFile;

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private static void fetchManifest() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://raw.githubusercontent.com/1ndiigo/packchooser-manifests/main/mainfest.json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            JsonObject object = gson.fromJson(response.body().string(), JsonObject.class);
            object.addProperty("timeDownloaded", Instant.now(Clock.systemUTC()).toEpochMilli());
            try {
                manifestFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + DatapackChooser.getModId());
                manifestFile.mkdirs();
                manifestFile = new File(FabricLoader.getInstance().getConfigDir().toString() + "/" + DatapackChooser.getModId() + "/manifest.json");
                manifestFile.createNewFile();
                FileWriter writer = new FileWriter(manifestFile);
                writer.write(gson.toJson(object));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            if (e instanceof JsonSyntaxException) {
                System.out.println("You are either offline or the manifest is bad, if the problem continues while online please file a issue https://github.com/1ndiigo/packchooser-manifests/issues");
            }
        }
    }
}

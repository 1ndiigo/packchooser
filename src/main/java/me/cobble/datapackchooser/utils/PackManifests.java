package me.cobble.datapackchooser.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class PackManifests {

    public static JsonObject fetchPacks() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setLenient().create();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://raw.githubusercontent.com/1ndiigo/packchooser-manifests/main/mainfest.json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return gson.fromJson(response.body().string(), JsonObject.class);
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            if (e instanceof JsonSyntaxException) {
                System.out.println("You are either offline or the manifest is bad, if the problem continues while online please file a issue https://github.com/1ndiigo/packchooser-manifests/issues");
            }
        }
        return null;
    }
}

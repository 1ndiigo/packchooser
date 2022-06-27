package me.cobble.packchooser.utils;

import me.cobble.packchooser.DatapackChooser;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FileDownloader {

    private final HttpClient client;

    public FileDownloader(HttpClient client) {
        this.client = client;
    }
    public Logger logger = DatapackChooser.getLogger();

    public void downloadDatapack(String url, File location, String fileName) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(HttpResponse::body).thenAccept(body -> {
            logger.info("Starting Datapack Download...");
            try {
                File file = new File(location.toString() + "/" + fileName + ".zip");
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(body);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logger.info("Datapack download complete!");
    }

    public void downloadResources(String url, String worldName) {
        if (url.equalsIgnoreCase("")) return;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(HttpResponse::body).thenAccept(body -> {
            logger.info("Starting download of resources...");
            try {
                File file = new File(MinecraftClient.getInstance().runDirectory.toString() + "/saves/" + worldName + "/resources.zip");
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(body);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        logger.info("Resources downloaded");
    }
}

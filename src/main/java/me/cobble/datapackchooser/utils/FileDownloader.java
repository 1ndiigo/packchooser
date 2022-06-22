package me.cobble.datapackchooser.utils;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloader implements AutoCloseable {

    private final OkHttpClient client;

    public FileDownloader(OkHttpClient client) {
        this.client = client;
    }

    public void download(String url, File location, String fileName) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: " + response);
                }

                File file = new File(location.toString() + "/" + fileName + ".zip");
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(response.body().bytes());
                fos.flush();
                fos.close();
            }
        });
    }

    @Override
    public void close() throws Exception {

    }
}

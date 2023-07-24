package net.datapackhub.datapacker.utils

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.datapackhub.datapacker.DatapackChooser.getModId
import net.fabricmc.loader.api.FabricLoader
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration


object Manifests {
    private val gson = Gson()
    private lateinit var manifestJson: JsonObject
    private val manifestFile = FabricLoader.getInstance().configDir.resolve(getModId())
    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build()

    fun downloadManifest() {
        val url = URL("https://raw.githubusercontent.com/HoodieRocks/packchooser-manifests/main/mainfest.json")

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url.toString()))
            .timeout(Duration.ofMinutes(1))
            .header("Content-Type", "application/json")
            .version(HttpClient.Version.HTTP_2)
            .GET()
            .build()

        manifestFile.toFile().mkdirs()
        manifestFile.resolve("manifest.json").toFile().createNewFile()

        val out = BufferedOutputStream(FileOutputStream(manifestFile.resolve("manifest.json").toFile()))

        client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
            .thenApplyAsync { obj: HttpResponse<InputStream> -> obj.body() }
            .thenAcceptAsync {
                manifestJson = gson.fromJson(it.readAllBytes().decodeToString(), JsonObject::class.java)
                it.transferTo(out)
            }
    }

    fun downloadPacks(packs: List<String>, file: Path) {
        packs.forEach { pack ->
            val url =
                getPacks().find { obj -> obj.asJsonObject["name"].asString == pack }!!.asJsonObject["url"].asString
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build()

            val stream = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApplyAsync { obj: HttpResponse<InputStream> -> obj.body() }

            BufferedOutputStream(FileOutputStream(file.toFile())).use { out ->
                stream.get().transferTo(out)
            }
        }
    }

    fun getPacks(): JsonArray {
        return manifestJson["packs"].asJsonArray
    }
}
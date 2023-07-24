package me.cobble.packchooser.utils

import net.datapackhub.datapacker.DatapackChooser.getLogger
import net.minecraft.client.MinecraftClient
import org.slf4j.Logger
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class FileDownloader(private val client: HttpClient) {
    private var logger: Logger = getLogger()
    fun downloadDatapack(url: String?, location: File, fileName: String) {
        val request = HttpRequest.newBuilder(URI.create(url)).build()
        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
            .thenApply { obj: HttpResponse<ByteArray?> -> obj.body() }
            .thenAccept { body: ByteArray? ->
                logger.info("Starting Datapack Download...")
                try {
                    val file = File("$location/$fileName.zip")
                    file.createNewFile()
                    val fos = FileOutputStream(file)
                    fos.write(body)
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        logger.info("Datapack download complete!")
    }

    fun downloadResources(url: String, worldName: String) {
        if (url.equals("", ignoreCase = true)) return
        val request = HttpRequest.newBuilder().uri(URI.create(url)).build()
        client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
            .thenApply { obj: HttpResponse<ByteArray?> -> obj.body() }
            .thenAccept { body: ByteArray? ->
                logger.info("Starting download of resources...")
                try {
                    val file =
                        File(MinecraftClient.getInstance().runDirectory.toString() + "/saves/" + worldName + "/resources.zip")
                    file.createNewFile()
                    val fos = FileOutputStream(file)
                    fos.write(body)
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        logger.info("Resources downloaded")
    }
}
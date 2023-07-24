package net.datapackhub.datapacker

import net.datapackhub.datapacker.utils.Manifests
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object DatapackChooser : ModInitializer {

    private const val MOD_ID = "packchooser"
    private val logger = LoggerFactory.getLogger(MOD_ID)

    //    private val configPath = FabricLoader.getInstance().configDir.resolve(getModId())
//    private val config = GsonConfigInstance(Config::class.java, configPath.resolve("config.json"))
    override fun onInitialize() {
        Manifests.downloadManifest()
    }

    fun getLogger() = logger

    fun getModId() = MOD_ID

}
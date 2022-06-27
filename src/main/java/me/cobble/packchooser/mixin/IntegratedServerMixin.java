package me.cobble.packchooser.mixin;

import com.mojang.datafixers.DataFixer;
import me.cobble.packchooser.utils.FileDownloader;
import me.cobble.packchooser.utils.PackManifests;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.net.http.HttpClient;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {

    public IntegratedServerMixin(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, apiServices, worldGenerationProgressListenerFactory);
    }

    @Inject(at = @At("RETURN"), method = "setupServer")
    private void addResources(CallbackInfoReturnable<Boolean> cir) {
        FileDownloader downloader = new FileDownloader(HttpClient.newHttpClient());
        downloader.downloadResources(PackManifests.getByName("More TNT").get("rp_url").getAsString(), this.getSaveProperties().getLevelName());
    }
}

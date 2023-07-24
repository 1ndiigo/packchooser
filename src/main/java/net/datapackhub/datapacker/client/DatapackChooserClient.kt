package net.datapackhub.datapacker.client

import net.datapackhub.datapacker.client.menus.DPHMarketplaceScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
class DatapackChooserClient : ClientModInitializer {
    override fun onInitializeClient() {
        val keybinding = KeyBindingHelper.registerKeyBinding(KeyBinding("datapacker.open_page", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "datapacker.keys"))

        ClientTickEvents.END_CLIENT_TICK.register {
            while (keybinding.wasPressed()) {
                it.setScreen(DPHMarketplaceScreen())
            }
        }
    }
}

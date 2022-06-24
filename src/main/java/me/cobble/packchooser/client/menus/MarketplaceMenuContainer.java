package me.cobble.packchooser.client.menus;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class MarketplaceMenuContainer extends CottonClientScreen {

    private static Screen parent;

    public static int GRID_CONSTANT = 18;

    public MarketplaceMenuContainer(Screen parent, GuiDescription description) {
        super(description);
        MarketplaceMenuContainer.parent = parent;

    }


    public static void closeScreen() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}

package me.cobble.packchooser.client.menus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import me.cobble.packchooser.DatapackChooser;
import me.cobble.packchooser.utils.FileDownloader;
import me.cobble.packchooser.utils.PackManifests;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.net.http.HttpClient;

public class MarketplaceRoot extends LightweightGuiDescription {

    int GRID_CONSTANT = MarketplaceMenuContainer.GRID_CONSTANT;
    WGridPanel detailsPanel = new WGridPanel(GRID_CONSTANT);
    WScrollPanel detailsRoot = new WScrollPanel(detailsPanel);
    JsonArray manifestCopy = DatapackChooser.getManifest().get("packs").getAsJsonArray();

    WGridPanel root = new WGridPanel(GRID_CONSTANT);
    public MarketplaceRoot(File file) {
        detailsPanel.setSize(7 * GRID_CONSTANT, 9 * GRID_CONSTANT);

        final WScrollPanel[] scrollPanel = {new WScrollPanel(listFromManifest(root, file, manifestCopy))};
        WButton doneButton = new WButton(ScreenTexts.DONE);
        WButton searchButton = new WButton(new TextureIcon(new Identifier("minecraft", "textures/mob_effect/mining_fatigue.png")));
        WTextField textField = new WTextField(Text.of("Search"));

        doneButton.setOnClick(MarketplaceMenuContainer::closeScreen);

        textField.setSize(5 * GRID_CONSTANT, GRID_CONSTANT);
        searchButton.setSize(GRID_CONSTANT, GRID_CONSTANT);

        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);
        root.setSize(300, 200);
        root.setHost(this);

        textField.setHost(this);
        textField.requestFocus();

        searchButton.setOnClick(() -> {
            manifestCopy = PackManifests.searchSimilar(textField.getText());
            root.remove(scrollPanel[0]);
            scrollPanel[0] = new WScrollPanel(listFromManifest(root, file, manifestCopy));
            scrollPanel[0].setScrollingVertically(TriState.TRUE);
            scrollPanel[0].setScrollingHorizontally(TriState.FALSE);
            root.add(scrollPanel[0], 0, 2, 8, 7);
        });

        scrollPanel[0].setScrollingVertically(TriState.TRUE);
        scrollPanel[0].setScrollingHorizontally(TriState.FALSE);

        root.add(textField, 0, 0, 6, 1);
        root.add(scrollPanel[0], 0, 2, 8, 7);
        root.add(searchButton, 7, 0, 1, 1);
        root.add(doneButton, 0, 10, 16, 1);

        root.validate(this);
    }


    private WPanel listFromManifest(WGridPanel root, File file, JsonArray manifest) {
        WGridPanel panel = new WGridPanel();

        for (int i = 0; i < manifest.size(); i++) {
            JsonObject object = manifest.get(i).getAsJsonObject();
            WButton button = new WButton(Text.of(object.get("name").getAsString()));
            button.setOnClick(() -> packDetails(root, button.getLabel(), file));
            panel.add(button, 0, i, 8, 1);
        }
        return panel;
    }

    private void packDetails(WGridPanel root, Text label, File file) {
        String name = label.getString();

        root.remove(detailsRoot);
        detailsPanel = new WGridPanel();
        detailsRoot = new WScrollPanel(detailsPanel);

        DatapackChooser.getManifest().get("packs").getAsJsonArray().forEach(pack -> {
            if (pack.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(name)) {
                WLabel title = new WLabel(label);
                WButton download = new WButton(Text.of("↓"));
                WText text = new WText(Text.of(pack.getAsJsonObject().get("description").getAsString()));
                text.setColor(0xFFF3F3F3);
                title.setColor(0xFFF3F3F3);

                download.setAlignment(HorizontalAlignment.CENTER);
                download.setOnClick(() -> {
                    HttpClient client = HttpClient.newHttpClient();
                    try {
                        FileDownloader downloader = new FileDownloader(client);
                        System.out.println(file.toString());
                        downloader.downloadDatapack(pack.getAsJsonObject().get("pack_url").getAsString(), file, name);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                download.addTooltip(new TooltipBuilder().add(Text.of("Click to download the pack!")));

                detailsPanel.add(title, 0, 0, 7, 1);
                detailsPanel.add(text, 0, 2, 7, 8);
                detailsPanel.add(download, 6, 0, 1, 1);
            }
        });

        root.add(detailsRoot, 9, 0, 8, 9);
    }

    @Override
    public void addPainters() {
        root.setBackgroundPainter(BackgroundPainter.createColorful(0xCC363636));
    }
}

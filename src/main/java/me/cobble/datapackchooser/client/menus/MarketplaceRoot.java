package me.cobble.datapackchooser.client.menus;

import com.google.gson.JsonObject;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import me.cobble.datapackchooser.DatapackChooser;
import me.cobble.datapackchooser.utils.FileDownloader;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MarketplaceRoot extends LightweightGuiDescription {

    WGridPanel detailsPanel = new WGridPanel();

    public MarketplaceRoot(File file) {
        detailsPanel.setSize(7 * 18, 9 * 18);

        WGridPanel root = new WGridPanel();
        WScrollPanel scrollPanel = new WScrollPanel(listFromManifest(root, file));
        WButton doneButton = new WButton(ScreenTexts.DONE);

        doneButton.setOnClick(MarketplaceMenuContainer::closeScreen);

        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL).setSize(300, 200);

        scrollPanel.setScrollingVertically(TriState.TRUE);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        root.add(scrollPanel, 0, 0, 7, 9);
        root.add(doneButton, 0, 10, 16, 1);

        root.validate(this);
    }

    private WPanel listFromManifest(WGridPanel root, File file) {
        JsonObject manifest = DatapackChooser.getManifest();
        WGridPanel panel = new WGridPanel();
        for (int i = 0; i < manifest.get("packs").getAsJsonArray().size(); i++) {
            JsonObject object = manifest.get("packs").getAsJsonArray().get(i).getAsJsonObject();
            WButton name = new WButton(Text.of(object.get("name").getAsString()));
            name.setSize(140, 20);
            name.setOnClick(() -> {
                packDetails(root, name.getLabel(), file);
            });
//            WLabel description;
//            WLabel url;
//            if(object.has("description")) {
//                description = new WLabel(Text.of(object.get("description").getAsString()));
//            } else {
//                description = new WLabel(Text.of("No description provided"));
//            }
//
//            if(object.has("url")) {
//                url = new WLabel(Text.of(object.get("url").getAsString()));
//            } else {
//                url = new WLabel(Text.of("No URL provided"));
//            }
            panel.add(name, 0, i, 7, 1);
        }
        return panel;
    }

    private void packDetails(WGridPanel root, Text label, File file) {
        String name = label.getString();

        root.remove(detailsPanel);
        detailsPanel = new WGridPanel();

        DatapackChooser.getManifest().get("packs").getAsJsonArray().forEach(pack -> {
            if (pack.getAsJsonObject().get("name").getAsString().equalsIgnoreCase(name)) {
                WLabel title = new WLabel(label);
                WButton download = new WButton(Text.of("Download"));

                download.setSize(7 * 18, 18);
                download.setAlignment(HorizontalAlignment.CENTER);
                download.setOnClick(() -> {
                    OkHttpClient client = new OkHttpClient();
                    try (FileDownloader downloader = new FileDownloader(client)) {
                        System.out.println(file.toString());
                        downloader.download(pack.getAsJsonObject().get("url").getAsString(), file, name);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                detailsPanel.add(title, 0, 0, 7, 1);

                AtomicInteger y = new AtomicInteger(2);

                Arrays.stream(pack.getAsJsonObject().get("description").getAsString().split("\n")).toList().forEach(string -> {
                    WLabel description = new WLabel(Text.of(string));
                    detailsPanel.add(description, 0, y.get(), 7, 3);
                    y.getAndIncrement();
                });

                detailsPanel.add(download, 0, y.get() + 1, 7, 1);
            }
        });

        root.add(detailsPanel, 8, 0, 8, 9);
    }
}

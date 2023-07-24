package net.datapackhub.datapacker.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.file.Path;

@Mixin(PackScreen.class)
public abstract class PackScreenMixin extends Screen {

    @Final
    private Screen parent;

    @Mutable
    @Shadow
    @Final
    private Path file;

    protected PackScreenMixin(Text title, Path file) {
        super(title);
        this.file = file;
    }

//    @Inject(at = @At("HEAD"), method = "init")
//    private void addButton(CallbackInfo ci) {
//        if (!(this.parent instanceof OptionsScreen)) {
//            this.addDrawableChild(
//                    ButtonWidget.builder(
//                            Text.of("Choose From Online"),
//                            button -> MinecraftClient.getInstance().setScreen(new TestElementa())
//                    ).dimensions(this.width / 2 - 154, this.height - 20, 308, 20).build());
//        }
//    }
}

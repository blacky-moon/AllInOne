package miyucomics.hexcassettes.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CassetteScreen extends Screen {
    public CassetteScreen() {
        super(Component.literal("Cassettes"));
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < ClientStorage.ownedCassettes; i++) {
            addRenderableWidget(new CassetteWidget(i, 16, i * 25 + 16));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
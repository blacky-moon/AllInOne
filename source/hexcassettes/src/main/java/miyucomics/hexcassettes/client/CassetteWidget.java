package miyucomics.hexcassettes.client;

import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.inits.HexcassettesNetworking;
import miyucomics.hexcassettes.inits.HexcassettesSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class CassetteWidget extends Button {
    private final int index;

    public CassetteWidget(int index, int x, int y) {
        super(x, y, 32, 16, Component.empty(), btn -> {}, DEFAULT_NARRATION);
        this.index = index;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Анимация: пульсация зелёного когда активна
        float pulse = 0f;
        if (isActive()) {
            // Синусоида от 0 до 1, период ~2 секунды
            pulse = (Mth.sin((Minecraft.getInstance().level.getGameTime() + partialTick) * 0.15f) + 1f) * 0.5f;
        }

        // Рисуем текстуру с тинтом
        int tint = isActive() ? blendColor(0xFF808080, 0xFF00FF44, pulse * 0.4f) : 0xFFFFFFFF;

        guiGraphics.setColor(
                FastColor.ARGB32.red(tint) / 255f,
                FastColor.ARGB32.green(tint) / 255f,
                FastColor.ARGB32.blue(tint) / 255f,
                1f
        );

        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0f, 0f, this.width, this.height, this.width, this.height);
        guiGraphics.setColor(1f, 1f, 1f, 1f); // сброс цвета

        // Текст справа
        Component textToDraw = isActive() ? ClientStorage.activeCassettes.get(index) : FREE_TEXT;
        var renderer = Minecraft.getInstance().font;

        // Текст тоже зеленеет когда активен
        int textColor = isActive() ? blendColor(0xFFFFFFFF, 0xFF00FF44, pulse * 0.5f) : 0xFFAAAAAA;
        guiGraphics.drawString(renderer, textToDraw, this.getX() + this.width + 2, this.getY() + 3, textColor);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(
                isActive() ? HexcassettesSounds.CASSETTE_EJECT.get() : HexcassettesSounds.CASSETTE_FAIL.get(),
                1.0f, 3.0f
        ));
    }

    @Override
    public void onPress() {
        if (isActive() && Minecraft.getInstance().level != null) {
            var payload = new HexcassettesNetworking.CassetteRemovePayload(
                    HexcassettesMain.serializeKey(
                            ClientStorage.activeCassettes.get(index),
                            Minecraft.getInstance().level.registryAccess()
                    )
            );
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
        }
    }

    public boolean isActive() {
        return index < ClientStorage.activeCassettes.size();
    }

    private static int blendColor(int color1, int color2, float ratio) {
        int r1 = FastColor.ARGB32.red(color1);
        int g1 = FastColor.ARGB32.green(color1);
        int b1 = FastColor.ARGB32.blue(color1);

        int r2 = FastColor.ARGB32.red(color2);
        int g2 = FastColor.ARGB32.green(color2);
        int b2 = FastColor.ARGB32.blue(color2);

        int r = Mth.floor(Mth.lerp(ratio, r1, r2));
        int g = Mth.floor(Mth.lerp(ratio, g1, g2));
        int b = Mth.floor(Mth.lerp(ratio, b1, b2));

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static final ResourceLocation TEXTURE = HexcassettesMain.id("textures/cassette.png");
    private static final Component FREE_TEXT = Component.translatable("hexcassettes.free")
            .withStyle(net.minecraft.ChatFormatting.DARK_GRAY, net.minecraft.ChatFormatting.ITALIC);
}
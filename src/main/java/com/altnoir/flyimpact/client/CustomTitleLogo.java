package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.Flyimpact;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class CustomTitleLogo {
    public static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "textures/gui/title/poopsky.png");
    public static final float CHANCE = 0.22F;
    private static final int TEXTURE_WIDTH = 546;
    private static final int TEXTURE_HEIGHT = 180;
    public static final int LOGO_WIDTH = 192;
    public static final int LOGO_HEIGHT = 63;
    public static final int LOGO_Y_SHIFT = -12;
    public static final float SPLASH_X_OFFSET = LOGO_WIDTH * (477.0F / TEXTURE_WIDTH - 0.5F) - 123.0F;
    public static final float SPLASH_Y_OFFSET = LOGO_Y_SHIFT + LOGO_HEIGHT - 44;

    private static boolean active;

    private CustomTitleLogo() {
    }

    public static void setActive(boolean value) {
        active = value;
    }

    public static boolean isActive() {
        return active;
    }

    public static void render(GuiGraphics graphics, int screenWidth, float alpha, int y) {
        int x = screenWidth / 2 - LOGO_WIDTH / 2;
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        graphics.blit(
                TEXTURE,
                x,
                y + LOGO_Y_SHIFT,
                LOGO_WIDTH,
                LOGO_HEIGHT,
                0.0F,
                0.0F,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}

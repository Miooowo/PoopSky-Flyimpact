package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.upgrade.UpgradePanelLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class UpgradeSidePanel {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("flyimpact", "textures/gui/upgrade_side.png");

    private UpgradeSidePanel() {
    }

    public static void render(GuiGraphics graphics, int leftPos, int topPos, int slotCount) {
        if (slotCount <= 0) {
            return;
        }
        int x = leftPos + UpgradePanelLayout.PANEL_OFFSET_X;
        int y = topPos + UpgradePanelLayout.PANEL_OFFSET_Y;
        blit(graphics, x, y, 0, 0, UpgradePanelLayout.PANEL_WIDTH, UpgradePanelLayout.TOP_CAP);
        for (int i = 0; i < slotCount; i++) {
            blit(
                    graphics,
                    x,
                    y + UpgradePanelLayout.TOP_CAP + i * UpgradePanelLayout.SLOT_SPACING,
                    0,
                    UpgradePanelLayout.TOP_CAP,
                    UpgradePanelLayout.PANEL_WIDTH,
                    UpgradePanelLayout.SLOT_SPACING);
        }
        blit(
                graphics,
                x,
                y + UpgradePanelLayout.TOP_CAP + slotCount * UpgradePanelLayout.SLOT_SPACING,
                0,
                UpgradePanelLayout.TOP_CAP + UpgradePanelLayout.SLOT_SPACING,
                UpgradePanelLayout.PANEL_WIDTH,
                UpgradePanelLayout.BOTTOM_CAP);
    }

    public static boolean contains(double mouseX, double mouseY, int leftPos, int topPos, int slotCount) {
        if (slotCount <= 0) {
            return false;
        }
        int x = leftPos + UpgradePanelLayout.PANEL_OFFSET_X;
        int y = topPos + UpgradePanelLayout.PANEL_OFFSET_Y;
        int width = UpgradePanelLayout.PANEL_WIDTH;
        int height = UpgradePanelLayout.panelHeight(slotCount);
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static void blit(GuiGraphics graphics, int x, int y, int u, int v, int width, int height) {
        graphics.blit(TEXTURE, x, y, u, v, width, height);
    }
}

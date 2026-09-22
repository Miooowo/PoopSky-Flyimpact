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
        int body = UpgradePanelLayout.heightWithoutBottom(slotCount);
        blit(graphics, x, y, 0, 0, UpgradePanelLayout.PANEL_WIDTH, UpgradePanelLayout.PANEL_TOP);
        blit(graphics, x, y + UpgradePanelLayout.PANEL_TOP, 0, UpgradePanelLayout.PANEL_TOP,
                UpgradePanelLayout.PANEL_BODY_WIDTH, body - UpgradePanelLayout.PANEL_TOP);
        blit(graphics, x, y + body, 0, UpgradePanelLayout.PANEL_BOTTOM_V,
                UpgradePanelLayout.PANEL_BODY_WIDTH, UpgradePanelLayout.PANEL_BOTTOM);
    }

    public static boolean contains(double mouseX, double mouseY, int leftPos, int topPos, int slotCount) {
        if (slotCount <= 0) {
            return false;
        }
        int x = leftPos + UpgradePanelLayout.PANEL_OFFSET_X;
        int y = topPos + UpgradePanelLayout.PANEL_OFFSET_Y;
        return mouseX >= x && mouseX < x + UpgradePanelLayout.PANEL_WIDTH
                && mouseY >= y && mouseY < y + UpgradePanelLayout.panelHeight(slotCount);
    }

    private static void blit(GuiGraphics graphics, int x, int y, int u, int v, int width, int height) {
        graphics.blit(TEXTURE, x, y, u, v, width, height);
    }
}

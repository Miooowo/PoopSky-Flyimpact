package com.altnoir.flyimpact.compat.jei;

import com.altnoir.flyimpact.client.FlyBarrelStorageGauges;
import com.altnoir.flyimpact.client.TieredFlyBarrelScreen;
import com.altnoir.flyimpact.menu.FlyBarrelStorageMenu;
import com.altnoir.flyimpact.upgrade.UpgradePanelLayout;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public final class FlyBarrelJeiGuiHandler<T extends AbstractContainerScreen<?>> implements IGuiContainerHandler<T> {
    @Override
    public List<Rect2i> getGuiExtraAreas(T screen) {
        List<Rect2i> areas = new ArrayList<>();
        int slots = screen instanceof TieredFlyBarrelScreen tiered ? tiered.getMenu().tier().upgradeSlots : 1;
        areas.add(new Rect2i(
                screen.getGuiLeft() + UpgradePanelLayout.PANEL_OFFSET_X,
                screen.getGuiTop() + UpgradePanelLayout.PANEL_OFFSET_Y,
                UpgradePanelLayout.PANEL_WIDTH,
                UpgradePanelLayout.panelHeight(slots)));
        if (screen.getMenu() instanceof FlyBarrelStorageMenu storage) {
            areas.addAll(FlyBarrelStorageGauges.extraAreas(screen.getGuiLeft(), screen.getGuiTop(), storage));
        }
        return areas;
    }
}

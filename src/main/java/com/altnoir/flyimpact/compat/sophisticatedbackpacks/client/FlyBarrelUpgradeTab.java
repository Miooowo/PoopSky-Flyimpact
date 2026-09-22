package com.altnoir.flyimpact.compat.sophisticatedbackpacks.client;

import com.altnoir.flyimpact.compat.sophisticatedbackpacks.FlyBarrelUpgradeContainer;
import com.altnoir.flyimpact.compat.sophisticatedbackpacks.FlyBarrelUpgradeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ProgressBar;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.TextureBlitData;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.UV;

import static net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper.GUI_CONTROLS;

public class FlyBarrelUpgradeTab extends UpgradeSettingsTab<FlyBarrelUpgradeContainer> {
    private static final TextureBlitData COOK_PROGRESS =
            new TextureBlitData(GUI_CONTROLS, Dimension.SQUARE_256, new UV(100, 239), new Dimension(22, 16));
    private static final int HIDDEN = -30000;
    private static final int PAD = 4;
    private static final int SLOT = 18;
    private static final int INPUT_X = PAD;
    private static final int INPUT_Y = 24;
    private static final int ARROW_X = INPUT_X + SLOT + 2;
    private static final int OUTPUT_COLS = 2;
    private static final int OUTPUT_X = ARROW_X + 22 + 2;
    private static final int OUTPUT_Y = 15;

    public FlyBarrelUpgradeTab(FlyBarrelUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) {
        super(
                upgradeContainer,
                position,
                screen,
                Component.translatable("gui.flyimpact.upgrades.fly_barrel"),
                Component.translatable("gui.flyimpact.upgrades.fly_barrel.tooltip"));
        openTabDimension = new Dimension(
                OUTPUT_X + OUTPUT_COLS * SLOT + PAD,
                OUTPUT_Y + 2 * SLOT + PAD);
        addHideableChild(new ProgressBar(
                new Position(x + ARROW_X, y + INPUT_Y + 1),
                COOK_PROGRESS,
                this::getProgress,
                ProgressBar.ProgressDirection.LEFT_RIGHT));
        hideSlots();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, Minecraft minecraft, int mouseX, int mouseY) {
        if (!getContainer().isOpen()) {
            hideSlots();
        }
        super.renderBg(guiGraphics, minecraft, mouseX, mouseY);
        if (!getContainer().isOpen()) {
            return;
        }
        GuiHelper.renderSlotsBackground(guiGraphics, x + INPUT_X, y + INPUT_Y, 1, 1);
        GuiHelper.renderSlotsBackground(guiGraphics, x + OUTPUT_X, y + OUTPUT_Y, OUTPUT_COLS, 2);
    }

    @Override
    protected void moveSlotsToTab() {
        if (!getContainer().isOpen()) {
            hideSlots();
            return;
        }
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();
        Slot input = getContainer().getSlots().get(FlyBarrelUpgradeWrapper.INPUT_SLOT);
        input.x = x + INPUT_X + 1 - guiLeft;
        input.y = y + INPUT_Y + 1 - guiTop;
        for (int i = 0; i < FlyBarrelUpgradeWrapper.OUTPUT_SLOTS; i++) {
            Slot output = getContainer().getSlots().get(i + 1);
            output.x = x + OUTPUT_X + 1 + (i % OUTPUT_COLS) * SLOT - guiLeft;
            output.y = y + OUTPUT_Y + 1 + (i / OUTPUT_COLS) * SLOT - guiTop;
        }
    }

    private void hideSlots() {
        for (Slot slot : getContainer().getSlots()) {
            slot.x = HIDDEN;
            slot.y = HIDDEN;
        }
    }

    private float getProgress() {
        FlyBarrelUpgradeWrapper wrapper = getContainer().getUpgradeWrapper();
        if (!wrapper.isProducing()) {
            return 0;
        }
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return 0;
        }
        int total = wrapper.getProduceTotal();
        if (total <= 0) {
            return 0;
        }
        return 1 - ((float) Math.max(wrapper.getProduceFinish() - level.getGameTime(), 0) / total);
    }
}

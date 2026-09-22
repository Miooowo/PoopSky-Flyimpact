package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.barrel.BarrelTier;
import com.altnoir.flyimpact.item.UpgradeModuleItem;
import com.altnoir.flyimpact.menu.TieredFlyBarrelMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class TieredFlyBarrelScreen extends AbstractContainerScreen<TieredFlyBarrelMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("poopsky", "textures/gui/fly_barrel.png");

    public TieredFlyBarrelScreen(TieredFlyBarrelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = menu.tier().imageHeight();
        this.inventoryLabelY = BarrelTier.HEADER_HEIGHT + menu.tier().guiOffset();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int extra = this.menu.tier().guiOffset();
        int header = BarrelTier.HEADER_HEIGHT;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, header);
        int extraRows = extra / BarrelTier.SLOT_SIZE;
        for (int row = 0; row < extraRows; row++) {
            graphics.blit(
                    TEXTURE,
                    x,
                    y + header + row * BarrelTier.SLOT_SIZE,
                    0,
                    BarrelTier.OUTPUT_Y,
                    this.imageWidth,
                    BarrelTier.SLOT_SIZE);
        }
        graphics.blit(
                TEXTURE,
                x,
                y + header + extra,
                0,
                header,
                this.imageWidth,
                BarrelTier.PLAYER_PANEL_HEIGHT);
        UpgradeSidePanel.render(graphics, x, y, this.menu.tier().upgradeSlots);
        FlyBarrelStorageGauges.render(graphics, x, y, this.menu);

        for (int i = 0; i < this.menu.tier().inputSlots; i++) {
            int maxProgress = this.menu.getMaxProgress(i);
            int progress = this.menu.getProgress(i);
            if (maxProgress <= 0 || progress <= 0) {
                continue;
            }
            int width = Math.min(BarrelTier.ARROW_WIDTH, (int) (progress * (float) BarrelTier.ARROW_WIDTH / maxProgress + 0.5F));
            if (width > 0) {
                graphics.blit(
                        TEXTURE,
                        x + BarrelTier.ARROW_X,
                        y + BarrelTier.ARROW_Y + i * BarrelTier.SLOT_SIZE,
                        176,
                        0,
                        width,
                        BarrelTier.ARROW_HEIGHT);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int hovered = hoveredInput(x, y, mouseX, mouseY);
        if (hovered >= 0 && this.menu.getMaxProgress(hovered) > 0) {
            graphics.renderComponentTooltip(this.font, progressTooltip(hovered), mouseX, mouseY);
        } else {
            FlyBarrelStorageGauges.renderTooltips(graphics, this.font, mouseX, mouseY, x, y, this.menu);
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        if (UpgradeSidePanel.contains(mouseX, mouseY, guiLeft, guiTop, this.menu.tier().upgradeSlots)
                || FlyBarrelStorageGauges.contains(mouseX, mouseY, guiLeft, guiTop, this.menu)) {
            return false;
        }
        return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (FlyBarrelStorageGauges.clickTank(this.menu, this.menu, x, y, mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int hoveredInput(int left, int top, int mouseX, int mouseY) {
        for (int i = 0; i < this.menu.tier().inputSlots; i++) {
            int arrowY = top + BarrelTier.ARROW_Y + i * BarrelTier.SLOT_SIZE;
            if (mouseX >= left + BarrelTier.ARROW_X
                    && mouseX < left + BarrelTier.ARROW_X + BarrelTier.ARROW_WIDTH
                    && mouseY >= arrowY
                    && mouseY < arrowY + BarrelTier.ARROW_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private List<Component> progressTooltip(int inputSlot) {
        int maxProgress = this.menu.getMaxProgress(inputSlot);
        int progress = this.menu.getProgress(inputSlot);
        int speed = 0;
        int yield = 0;
        int transfer = 0;
        boolean fluid = false;
        boolean energy = false;
        for (int i = 0; i < this.menu.tier().upgradeSlots; i++) {
            ItemStack upgrade = this.menu.upgradeInSlot(i);
            speed += UpgradeModuleItem.speedPercent(upgrade);
            yield += UpgradeModuleItem.yieldPercent(upgrade);
            transfer = Math.max(transfer, UpgradeModuleItem.transferMultiplier(upgrade));
            fluid = fluid || UpgradeModuleItem.hasFluid(upgrade);
            energy = energy || UpgradeModuleItem.hasEnergy(upgrade);
        }
        List<Component> lines = new ArrayList<>();
        if (maxProgress > 0) {
            lines.add(Component.literal(String.format("%.1f%%", progress * 100.0F / maxProgress)).withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable("tooltip.flyimpact.barrel_interval", String.format("%.1f", maxProgress / 20.0F)).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (speed > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.speed_bonus", speed).withStyle(ChatFormatting.AQUA));
        }
        if (yield > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.yield_bonus", yield).withStyle(ChatFormatting.GOLD));
        }
        if (transfer > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.transfer_enabled", transfer).withStyle(ChatFormatting.GREEN));
        }
        if (fluid) {
            lines.add(Component.translatable("tooltip.flyimpact.fluid_enabled").withStyle(ChatFormatting.BLUE));
        }
        if (energy) {
            lines.add(Component.translatable("tooltip.flyimpact.energy_enabled").withStyle(ChatFormatting.RED));
        }
        return lines;
    }
}

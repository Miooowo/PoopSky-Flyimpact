package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.breeding.SimpleBreedingChestSlots;
import com.altnoir.flyimpact.menu.SimpleBreedingChestMenu;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class SimpleBreedingChestScreen extends AbstractContainerScreen<SimpleBreedingChestMenu> {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("poopsky", "textures/gui/breeding_chest.png");
    private static final ResourceLocation HEART =
            ResourceLocation.withDefaultNamespace("textures/particle/heart.png");

    public SimpleBreedingChestScreen(SimpleBreedingChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = SimpleBreedingChestSlots.IMAGE_WIDTH;
        this.imageHeight = SimpleBreedingChestSlots.IMAGE_HEIGHT;
        this.inventoryLabelY = SimpleBreedingChestSlots.INVENTORY_LABEL_Y;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, SimpleBreedingChestSlots.MACHINE_HEIGHT);
        blitExtraRowBackground(graphics, x, y);
        graphics.blit(
                BACKGROUND,
                x,
                y + SimpleBreedingChestSlots.PLAYER_PANEL_Y,
                0,
                SimpleBreedingChestSlots.PLAYER_PANEL_V,
                this.imageWidth,
                SimpleBreedingChestSlots.PLAYER_PANEL_HEIGHT);

        for (int i = 0; i < SimpleBreedingChestSlots.ACCEL_COUNT; i++) {
            blitSlot(graphics, x + SimpleBreedingChestSlots.accelX(i), y + SimpleBreedingChestSlots.EXTRA_SLOT_Y);
        }
        for (int i = 0; i < SimpleBreedingChestSlots.YIELD_COUNT; i++) {
            blitSlot(graphics, x + SimpleBreedingChestSlots.yieldX(i), y + SimpleBreedingChestSlots.EXTRA_SLOT_Y);
        }

        int maxProgress = this.menu.getMaxProgress();
        int progress = this.menu.getProgress();
        if (maxProgress > 0) {
            int width = Math.min(
                    SimpleBreedingChestSlots.PROGRESS_WIDTH,
                    (int) (progress / (float) maxProgress * SimpleBreedingChestSlots.PROGRESS_WIDTH + 0.5F));
            if (width > 0) {
                graphics.blit(
                        BACKGROUND,
                        x + SimpleBreedingChestSlots.PROGRESS_X,
                        y + SimpleBreedingChestSlots.PROGRESS_Y,
                        SimpleBreedingChestSlots.PROGRESS_U,
                        0,
                        width,
                        SimpleBreedingChestSlots.PROGRESS_HEIGHT);
            }
        }

        if (maxProgress > 0 && progress > 0) {
            long time = this.minecraft != null && this.minecraft.level != null ? this.minecraft.level.getGameTime() : 0L;
            int bob = (int) Math.round(Math.sin((time + partialTick) / 6.0F) * 2.0F);
            graphics.blit(HEART, x + 95, y + 16 + bob, 0, 0, 8, 8, 8, 8);
            graphics.drawString(this.font, stage(progress, maxProgress).copy().withStyle(ChatFormatting.DARK_GRAY), x + 84, y + 8, 0x404040, false);
        }
    }

    private static void blitExtraRowBackground(GuiGraphics graphics, int x, int y) {
        int destY = y + SimpleBreedingChestSlots.MACHINE_HEIGHT;
        int remaining = SimpleBreedingChestSlots.PLAYER_PANEL_Y - SimpleBreedingChestSlots.MACHINE_HEIGHT;
        while (remaining > 0) {
            int height = Math.min(SimpleBreedingChestSlots.FILL_HEIGHT, remaining);
            graphics.blit(
                    BACKGROUND,
                    x,
                    destY,
                    0,
                    SimpleBreedingChestSlots.FILL_V,
                    SimpleBreedingChestSlots.IMAGE_WIDTH,
                    height);
            destY += height;
            remaining -= height;
        }
    }

    private static void blitSlot(GuiGraphics graphics, int x, int y) {
        graphics.blit(
                BACKGROUND,
                x - 1,
                y - 1,
                SimpleBreedingChestSlots.SLOT_SPRITE_U,
                SimpleBreedingChestSlots.SLOT_SPRITE_V,
                SimpleBreedingChestSlots.SLOT_SPRITE,
                SimpleBreedingChestSlots.SLOT_SPRITE);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + SimpleBreedingChestSlots.PROGRESS_X
                && mouseX < x + SimpleBreedingChestSlots.PROGRESS_X + SimpleBreedingChestSlots.PROGRESS_WIDTH
                && mouseY >= y + SimpleBreedingChestSlots.PROGRESS_Y
                && mouseY < y + SimpleBreedingChestSlots.PROGRESS_Y + SimpleBreedingChestSlots.PROGRESS_HEIGHT
                && this.menu.getMaxProgress() > 0) {
            graphics.renderComponentTooltip(this.font, progressTooltip(), mouseX, mouseY);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            Component hint = emptySlotHint(this.hoveredSlot);
            if (hint != null) {
                graphics.renderTooltip(this.font, hint, mouseX, mouseY);
                return;
            }
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    private static @org.jetbrains.annotations.Nullable Component emptySlotHint(Slot slot) {
        int index = slot.index;
        if (index >= SimpleBreedingChestSlots.ACCEL_START
                && index < SimpleBreedingChestSlots.ACCEL_START + SimpleBreedingChestSlots.ACCEL_COUNT) {
            return Component.translatable("tooltip.flyimpact.simple_breeding_chest.efficiency");
        }
        if (index >= SimpleBreedingChestSlots.YIELD_START
                && index < SimpleBreedingChestSlots.YIELD_START + SimpleBreedingChestSlots.YIELD_COUNT) {
            return Component.translatable("tooltip.flyimpact.simple_breeding_chest.yield");
        }
        return null;
    }

    private List<Component> progressTooltip() {
        int maxProgress = this.menu.getMaxProgress();
        int progress = this.menu.getProgress();
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(String.format("%.1f%%", progress * 100.0F / maxProgress)).withStyle(ChatFormatting.GRAY));
        lines.add(stage(progress, maxProgress).copy().withStyle(ChatFormatting.DARK_GRAY));
        ItemStack parent1 = this.menu.slots.get(SimpleBreedingChestSlots.FLY_1).getItem();
        ItemStack parent2 = this.menu.slots.get(SimpleBreedingChestSlots.FLY_2).getItem();
        if (FlyItem.isFlyItem(parent1) && FlyItem.isFlyItem(parent2)) {
            boolean same = FlyItem.getFlyType(parent1).id().equals(FlyItem.getFlyType(parent2).id());
            lines.add(Component.translatable(same
                    ? "tooltip.flyimpact.breeding.purifying"
                    : "tooltip.flyimpact.breeding.crossing").withStyle(same ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        }
        lines.add(Component.translatable("tooltip.flyimpact.barrel_interval", String.format("%.1f", maxProgress / 20.0F))
                .withStyle(ChatFormatting.DARK_GRAY));
        if (this.menu.getPoopBonus() > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.simple_breeding_chest.poop_bonus", this.menu.getPoopBonus())
                    .withStyle(ChatFormatting.AQUA));
        }
        if (this.menu.getMaggotsBonus() > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.simple_breeding_chest.maggot_bonus", this.menu.getMaggotsBonus())
                    .withStyle(ChatFormatting.GOLD));
        }
        return lines;
    }

    private static Component stage(int progress, int maxProgress) {
        float ratio = progress / (float) maxProgress;
        String key;
        if (ratio < 0.33F) {
            key = "tooltip.flyimpact.breeding.stage_synapsis";
        } else if (ratio < 0.66F) {
            key = "tooltip.flyimpact.breeding.stage_segregation";
        } else {
            key = "tooltip.flyimpact.breeding.stage_assortment";
        }
        return Component.translatable(key);
    }
}

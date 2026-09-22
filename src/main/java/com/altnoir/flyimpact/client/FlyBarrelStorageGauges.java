package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.menu.FlyBarrelStorageMenu;
import com.altnoir.flyimpact.network.BarrelTankClickPayload;
import com.altnoir.flyimpact.upgrade.UpgradePanelLayout;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 苍蝇桶液体罐 / 能量条。布局与精妙背包储罐、电池升级一致：18 宽竖条、流体 still 贴图、黄到红电量段。
 * 画在左侧升级栏外侧，避免被 JEI 默认的右侧物品栏挡住。
 */
public final class FlyBarrelStorageGauges {
    public static final int WIDTH = 18;
    public static final int HEIGHT = 54;
    public static final int INNER = 16;
    public static final int GAP = 4;
    public static final int CHARGE_SEGMENT = 6;
    private static final int FRAME_SHADOW = 0xFF373737;
    private static final int FRAME_HIGHLIGHT = 0xFFFFFFFF;
    private static final int FRAME_EDGE = 0xFF8B8B8B;
    private static final int INNER_BG = 0xFF101010;
    private static final int HATCH = 0x58FFFFFF;
    private static final int ENERGY_BOTTOM = 0xFFFF40;
    private static final int ENERGY_TOP = 0xFF1A1A;

    private FlyBarrelStorageGauges() {
    }

    public static int panelWidth(FlyBarrelStorageMenu menu) {
        int count = gaugeCount(menu);
        if (count == 0) {
            return 0;
        }
        return count * WIDTH + (count - 1) * GAP;
    }

    public static int originX(int leftPos, FlyBarrelStorageMenu menu) {
        int width = panelWidth(menu);
        if (width <= 0) {
            return leftPos;
        }
        return leftPos + UpgradePanelLayout.PANEL_OFFSET_X - GAP - width;
    }

    public static int originY(int topPos) {
        return topPos + UpgradePanelLayout.SLOT_Y;
    }

    public static List<Rect2i> extraAreas(int leftPos, int topPos, FlyBarrelStorageMenu menu) {
        int width = panelWidth(menu);
        if (width <= 0) {
            return List.of();
        }
        return List.of(new Rect2i(originX(leftPos, menu), originY(topPos), width, HEIGHT));
    }

    public static boolean contains(double mouseX, double mouseY, int leftPos, int topPos, FlyBarrelStorageMenu menu) {
        int width = panelWidth(menu);
        if (width <= 0) {
            return false;
        }
        int x = originX(leftPos, menu);
        int y = originY(topPos);
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + HEIGHT;
    }

    public static void render(GuiGraphics graphics, int leftPos, int topPos, FlyBarrelStorageMenu menu) {
        int x = originX(leftPos, menu);
        int y = originY(topPos);
        if (menu.flyimpact$hasFluidUpgrade()) {
            renderTank(graphics, x, y, menu.flyimpact$fluidStack(), menu.flyimpact$fluidCapacity());
            x += WIDTH + GAP;
        }
        if (menu.flyimpact$hasEnergyUpgrade()) {
            renderEnergy(graphics, x, y, menu.flyimpact$energyStored(), menu.flyimpact$energyCapacity());
        }
    }

    public static void renderTooltips(
            GuiGraphics graphics,
            Font font,
            int mouseX,
            int mouseY,
            int leftPos,
            int topPos,
            FlyBarrelStorageMenu menu) {
        int x = originX(leftPos, menu);
        int y = originY(topPos);
        if (menu.flyimpact$hasFluidUpgrade()) {
            if (overGauge(mouseX, mouseY, x, y)) {
                graphics.renderComponentTooltip(font, fluidTooltip(menu), mouseX, mouseY);
                return;
            }
            x += WIDTH + GAP;
        }
        if (menu.flyimpact$hasEnergyUpgrade() && overGauge(mouseX, mouseY, x, y)) {
            graphics.renderComponentTooltip(font, energyTooltip(menu), mouseX, mouseY);
        }
    }

    public static boolean clickTank(
            AbstractContainerMenu menu,
            FlyBarrelStorageMenu storage,
            int leftPos,
            int topPos,
            double mouseX,
            double mouseY) {
        if (!storage.flyimpact$hasFluidUpgrade()) {
            return false;
        }
        int x = originX(leftPos, storage);
        int y = originY(topPos);
        if (!overGauge(mouseX, mouseY, x, y)) {
            return false;
        }
        ItemStack carried = menu.getCarried();
        if (!carried.isEmpty() && carried.getCount() > 1) {
            return false;
        }
        PacketDistributor.sendToServer(new BarrelTankClickPayload(menu.containerId));
        return true;
    }

    private static int gaugeCount(FlyBarrelStorageMenu menu) {
        int count = 0;
        if (menu.flyimpact$hasFluidUpgrade()) {
            count++;
        }
        if (menu.flyimpact$hasEnergyUpgrade()) {
            count++;
        }
        return count;
    }

    private static boolean overGauge(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
    }

    private static List<Component> fluidTooltip(FlyBarrelStorageMenu menu) {
        List<Component> lines = new ArrayList<>();
        FluidStack fluid = menu.flyimpact$fluidStack();
        if (fluid.isEmpty()) {
            lines.add(Component.translatable("tooltip.flyimpact.tank.empty").withStyle(ChatFormatting.GRAY));
        } else {
            lines.add(fluid.getHoverName());
        }
        lines.add(Component.translatable(
                "tooltip.flyimpact.tank.contents",
                String.format("%,d", fluid.getAmount()),
                String.format("%,d", menu.flyimpact$fluidCapacity())).withStyle(ChatFormatting.DARK_GRAY));
        return lines;
    }

    private static List<Component> energyTooltip(FlyBarrelStorageMenu menu) {
        return List.of(Component.translatable(
                "tooltip.flyimpact.battery.contents",
                String.format("%,d", menu.flyimpact$energyStored()),
                String.format("%,d", menu.flyimpact$energyCapacity())));
    }

    private static void renderTank(GuiGraphics graphics, int x, int y, FluidStack fluid, int capacity) {
        blitTankBackground(graphics, x, y);
        if (!fluid.isEmpty() && capacity > 0) {
            int fillHeight = Math.max(1, Math.round((HEIGHT - 2) * (fluid.getAmount() / (float) capacity)));
            fillHeight = Math.min(HEIGHT - 2, fillHeight);
            int fluidY = fluid.getFluidType().isLighterThanAir() ? y + 1 : y + HEIGHT - 1 - fillHeight;
            renderFluid(graphics, fluid, x + 1, fluidY, fillHeight);
        }
        blitOverlay(graphics, x, y);
    }

    private static void renderEnergy(GuiGraphics graphics, int x, int y, int stored, int capacity) {
        blitTankBackground(graphics, x, y);
        if (stored > 0 && capacity > 0) {
            int segments = HEIGHT / CHARGE_SEGMENT;
            int filled = Math.min(segments, Math.max(1, Math.round(segments * (stored / (float) capacity))));
            int bottomR = ENERGY_BOTTOM >> 16 & 255;
            int bottomG = ENERGY_BOTTOM >> 8 & 255;
            int bottomB = ENERGY_BOTTOM & 255;
            int topR = ENERGY_TOP >> 16 & 255;
            int topG = ENERGY_TOP >> 8 & 255;
            int topB = ENERGY_TOP & 255;
            for (int i = 0; i < filled; i++) {
                float t = segments <= 1 ? 0 : i / (float) (segments - 1);
                int r = (int) (bottomR * (1 - t) + topR * t);
                int g = (int) (bottomG * (1 - t) + topG * t);
                int b = (int) (bottomB * (1 - t) + topB * t);
                int color = 0xFF000000 | r << 16 | g << 8 | b;
                int segY = y + HEIGHT - 1 - (i + 1) * CHARGE_SEGMENT;
                graphics.fill(x + 1, segY, x + 1 + INNER, segY + CHARGE_SEGMENT, color);
            }
        }
        blitOverlay(graphics, x, y);
    }

    private static void blitTankBackground(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + WIDTH, y + HEIGHT, FRAME_EDGE);
        graphics.fill(x, y, x + WIDTH, y + 1, FRAME_SHADOW);
        graphics.fill(x, y, x + 1, y + HEIGHT, FRAME_SHADOW);
        graphics.fill(x + WIDTH - 1, y, x + WIDTH, y + HEIGHT, FRAME_HIGHLIGHT);
        graphics.fill(x, y + HEIGHT - 1, x + WIDTH, y + HEIGHT, FRAME_HIGHLIGHT);
        graphics.fill(x + 1, y + 1, x + WIDTH - 1, y + HEIGHT - 1, INNER_BG);
    }

    private static void blitOverlay(GuiGraphics graphics, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        for (int i = CHARGE_SEGMENT; i < HEIGHT - 1; i += CHARGE_SEGMENT) {
            int lineY = y + HEIGHT - 1 - i;
            graphics.fill(x + 1, lineY, x + 5, lineY + 1, HATCH);
        }
        graphics.pose().popPose();
    }

    private static void renderFluid(GuiGraphics graphics, FluidStack fluid, int x, int y, int height) {
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation texture = props.getStillTexture(fluid);
        if (texture == null) {
            graphics.fill(x, y, x + INNER, y + height, 0xFF3F76E4);
            return;
        }
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        int color = props.getTintColor(fluid);
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        graphics.setColor(red, green, blue, 1.0F);
        RenderSystem.enableBlend();
        int remaining = height;
        int drawY = y;
        int tile = Math.max(1, sprite.contents().height());
        while (remaining > 0) {
            int slice = Math.min(tile, remaining);
            graphics.blit(x, drawY, 0, INNER, slice, sprite);
            drawY += slice;
            remaining -= slice;
        }
        RenderSystem.disableBlend();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

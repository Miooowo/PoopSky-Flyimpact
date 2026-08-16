package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.item.UpgradeModuleItem;
import com.altnoir.poopsky.client.inventory.FlyBarrelMenu;
import com.altnoir.poopsky.client.inventory.FlyBarrelScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(FlyBarrelScreen.class)
public abstract class FlyBarrelScreenMixin extends AbstractContainerScreen<FlyBarrelMenu> {
    protected FlyBarrelScreenMixin(FlyBarrelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void flyimpact$drawUpgradeSlot(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath("poopsky", "textures/gui/fly_barrel.png");
        graphics.blit(background, x + 7, y + 19, 7, 50, 18, 18);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;II)V"
            ),
            cancellable = true
    )
    private void flyimpact$replaceProgressTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int maxProgress = this.menu.getMaxProgress();
        int progress = this.menu.getProgress();
        ItemStack upgrade = this.menu.slots.get(this.menu.slots.size() - 1).getItem();
        int speed = UpgradeModuleItem.speedPercent(upgrade);
        int yield = UpgradeModuleItem.yieldPercent(upgrade);

        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(String.format("%.1f%%", progress * 100.0F / maxProgress)).withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("tooltip.flyimpact.barrel_interval", flyimpact$formatSeconds(maxProgress)).withStyle(ChatFormatting.DARK_GRAY));
        if (speed > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.speed_bonus", speed).withStyle(ChatFormatting.AQUA));
        }
        if (yield > 0) {
            lines.add(Component.translatable("tooltip.flyimpact.yield_bonus", yield).withStyle(ChatFormatting.GOLD));
        }
        graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        ci.cancel();
    }

    @Unique
    private static String flyimpact$formatSeconds(int ticks) {
        return String.format("%.1f", ticks / 20.0F);
    }
}

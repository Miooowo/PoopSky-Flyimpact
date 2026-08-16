package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.FlyLineage;
import com.altnoir.poopsky.client.inventory.BreedingChestMenu;
import com.altnoir.poopsky.client.inventory.BreedingChestScreen;
import com.altnoir.poopsky.content.item.p.FlyItem;
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

@Mixin(BreedingChestScreen.class)
public abstract class BreedingChestScreenMixin extends AbstractContainerScreen<BreedingChestMenu> {
    @Unique
    private static final ResourceLocation FLYIMPACT_HEART =
            ResourceLocation.withDefaultNamespace("textures/particle/heart.png");

    protected BreedingChestScreenMixin(BreedingChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void flyimpact$drawCrossing(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        int maxProgress = this.menu.getMaxProgress();
        int progress = this.menu.getProgress();
        if (maxProgress <= 0 || progress <= 0) {
            return;
        }
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        long time = this.minecraft != null && this.minecraft.level != null
                ? this.minecraft.level.getGameTime()
                : 0L;
        int bob = (int) Math.round(Math.sin((time + partialTick) / 6.0F) * 2.0F);
        graphics.blit(FLYIMPACT_HEART, x + 95, y + 16 + bob, 0, 0, 8, 8, 8, 8);

        ItemStack parent1 = this.menu.slots.get(1).getItem();
        ItemStack parent2 = this.menu.slots.get(2).getItem();
        if (!FlyItem.isFlyItem(parent1) || !FlyItem.isFlyItem(parent2)) {
            return;
        }
        Component stage = flyimpact$stage(progress, maxProgress)
                .copy()
                .withStyle(ChatFormatting.DARK_GRAY);
        graphics.drawString(this.font, stage, x + 84, y + 8, 0x404040, false);
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
        ItemStack parent1 = this.menu.slots.get(1).getItem();
        ItemStack parent2 = this.menu.slots.get(2).getItem();

        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(String.format("%.1f%%", progress * 100.0F / maxProgress)).withStyle(ChatFormatting.GRAY));
        lines.add(flyimpact$stage(progress, maxProgress).copy().withStyle(ChatFormatting.DARK_GRAY));
        if (FlyItem.isFlyItem(parent1) && FlyItem.isFlyItem(parent2)) {
            boolean purifying = FlyLineage.sameVariety(parent1, parent2);
            lines.add(Component.translatable(purifying
                    ? "tooltip.flyimpact.breeding.purifying"
                    : "tooltip.flyimpact.breeding.crossing").withStyle(purifying ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
            lines.add(FlyLineage.generationLabel(FlyLineage.childGeneration(parent1, parent2)).withStyle(ChatFormatting.GRAY));
        }
        graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        ci.cancel();
    }

    @Unique
    private static Component flyimpact$stage(int progress, int maxProgress) {
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

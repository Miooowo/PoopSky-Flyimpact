package com.altnoir.flyimpact;

import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class FlyLineageEvents {
    private FlyLineageEvents() {
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!FlyItem.isFlyItem(stack)) {
            return;
        }
        int generation = FlyLineage.generation(stack);
        int purity = FlyLineage.purity(stack);
        event.getToolTip().add(FlyLineage.generationLabel(generation).withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(FlyLineage.purityLabel(purity).withStyle(FlyLineage.purityColor(purity)));
    }
}

package com.altnoir.flyimpact.event;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

public final class FlyFuel {
    public static final int COAL_FLY_BURN_TIME = 1600;

    private FlyFuel() {
    }

    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        if (isCoalFly(event.getItemStack())) {
            event.setBurnTime(COAL_FLY_BURN_TIME);
        }
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!isCoalFly(event.getItemStack())) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.flyimpact.coal_fly.fuel").withStyle(ChatFormatting.GRAY));
    }

    private static boolean isCoalFly(ItemStack stack) {
        return FlyItem.isFlyItem(stack) && Flyimpact.FLY_TYPE_COAL.equals(FlyItem.getFlyType(stack).id());
    }
}

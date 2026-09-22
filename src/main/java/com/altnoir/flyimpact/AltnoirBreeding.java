package com.altnoir.flyimpact;

import com.altnoir.flyimpact.item.FlyimpactComponents;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class AltnoirBreeding {
    public static final int REQUIRED_BREEDS = 99;

    private AltnoirBreeding() {
    }

    public static void afterBreed(ItemStackHandler handler, int flySlot1, int flySlot2) {
        incrementParent(handler, flySlot1);
        incrementParent(handler, flySlot2);
    }

    private static void incrementParent(ItemStackHandler handler, int slot) {
        ItemStack stack = handler.getStackInSlot(slot);
        if (!FlyItem.isFlyItem(stack)) {
            return;
        }
        if (!Flyimpact.FLY_TYPE_NORMAL.equals(FlyItem.getFlyType(stack).id())) {
            return;
        }
        ItemStack copy = stack.copy();
        int count = copy.getOrDefault(FlyimpactComponents.BREED_COUNT.get(), 0) + 1;
        if (count >= REQUIRED_BREEDS) {
            ItemStack converted = FlyItem.withType(Flyimpact.FLY_TYPE_ALTNOIR);
            converted.setCount(copy.getCount());
            handler.setStackInSlot(slot, converted);
            return;
        }
        copy.set(FlyimpactComponents.BREED_COUNT.get(), count);
        handler.setStackInSlot(slot, copy);
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!FlyItem.isFlyItem(stack)) {
            return;
        }
        if (!Flyimpact.FLY_TYPE_NORMAL.equals(FlyItem.getFlyType(stack).id())) {
            return;
        }
        int count = stack.getOrDefault(FlyimpactComponents.BREED_COUNT.get(), 0);
        if (count <= 0) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.flyimpact.breed_count", count, REQUIRED_BREEDS)
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

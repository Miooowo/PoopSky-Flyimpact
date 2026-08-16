package com.altnoir.flyimpact.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeModuleItem extends Item {
    public enum Kind {
        YIELD,
        SPEED
    }

    private final Kind kind;
    private final int percent;

    public UpgradeModuleItem(Properties properties, Kind kind, int percent) {
        super(properties);
        this.kind = kind;
        this.percent = percent;
    }

    public Kind kind() {
        return kind;
    }

    public int percent() {
        return percent;
    }

    public static boolean isUpgrade(ItemStack stack) {
        return stack.getItem() instanceof UpgradeModuleItem;
    }

    public static int yieldPercent(ItemStack stack) {
        if (stack.getItem() instanceof UpgradeModuleItem item && item.kind == Kind.YIELD) {
            return item.percent;
        }
        return 0;
    }

    public static int speedPercent(ItemStack stack) {
        if (stack.getItem() instanceof UpgradeModuleItem item && item.kind == Kind.SPEED) {
            return item.percent;
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String key = kind == Kind.YIELD ? "tooltip.flyimpact.yield_bonus" : "tooltip.flyimpact.speed_bonus";
        tooltipComponents.add(Component.translatable(key, percent).withStyle(ChatFormatting.GRAY));
    }
}

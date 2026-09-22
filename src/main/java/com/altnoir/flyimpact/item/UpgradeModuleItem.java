package com.altnoir.flyimpact.item;

import com.altnoir.flyimpact.barrel.FlyBarrelUpgradeAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeModuleItem extends Item {
    public enum Kind {
        YIELD,
        SPEED,
        TRANSFER,
        FLUID,
        ENERGY
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

    public static boolean hasTransfer(ItemStack stack) {
        return stack.getItem() instanceof UpgradeModuleItem item && item.kind == Kind.TRANSFER;
    }

    public static int transferMultiplier(ItemStack stack) {
        if (stack.getItem() instanceof UpgradeModuleItem item && item.kind == Kind.TRANSFER) {
            return Math.max(1, item.percent);
        }
        return 0;
    }

    public static boolean hasFluid(ItemStack stack) {
        return stack.getItem() instanceof UpgradeModuleItem item && item.kind == Kind.FLUID;
    }

    public static boolean hasEnergy(ItemStack stack) {
        return stack.getItem() instanceof UpgradeModuleItem item && item.kind == Kind.ENERGY;
    }

    public static int yieldPercent(FlyBarrelUpgradeAccess access) {
        int total = 0;
        for (int slot = 0; slot < access.flyimpact$upgradeSlots(); slot++) {
            total += yieldPercent(access.flyimpact$getUpgrade(slot));
        }
        return total;
    }

    public static int speedPercent(FlyBarrelUpgradeAccess access) {
        int total = 0;
        for (int slot = 0; slot < access.flyimpact$upgradeSlots(); slot++) {
            total += speedPercent(access.flyimpact$getUpgrade(slot));
        }
        return total;
    }

    public static boolean hasTransfer(FlyBarrelUpgradeAccess access) {
        return transferMultiplier(access) > 0;
    }

    public static int transferMultiplier(FlyBarrelUpgradeAccess access) {
        int best = 0;
        for (int slot = 0; slot < access.flyimpact$upgradeSlots(); slot++) {
            best = Math.max(best, transferMultiplier(access.flyimpact$getUpgrade(slot)));
        }
        return best;
    }

    public static boolean hasFluid(FlyBarrelUpgradeAccess access) {
        for (int slot = 0; slot < access.flyimpact$upgradeSlots(); slot++) {
            if (hasFluid(access.flyimpact$getUpgrade(slot))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEnergy(FlyBarrelUpgradeAccess access) {
        for (int slot = 0; slot < access.flyimpact$upgradeSlots(); slot++) {
            if (hasEnergy(access.flyimpact$getUpgrade(slot))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        switch (kind) {
            case TRANSFER -> tooltipComponents.add(
                    Component.translatable("tooltip.flyimpact.transfer_upgrade", Math.max(1, percent)).withStyle(ChatFormatting.GRAY));
            case FLUID -> tooltipComponents.add(
                    Component.translatable("tooltip.flyimpact.fluid_upgrade").withStyle(ChatFormatting.GRAY));
            case ENERGY -> tooltipComponents.add(
                    Component.translatable("tooltip.flyimpact.energy_upgrade").withStyle(ChatFormatting.GRAY));
            case YIELD -> tooltipComponents.add(
                    Component.translatable("tooltip.flyimpact.yield_bonus", percent).withStyle(ChatFormatting.GRAY));
            case SPEED -> tooltipComponents.add(
                    Component.translatable("tooltip.flyimpact.speed_bonus", percent).withStyle(ChatFormatting.GRAY));
        }
    }
}

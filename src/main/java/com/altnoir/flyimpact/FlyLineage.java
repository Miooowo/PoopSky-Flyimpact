package com.altnoir.flyimpact;

import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public final class FlyLineage {
    public static final int MAX_GENERATION = 8;

    public static final int PURITY_WILD = 0;
    public static final int PURITY_HYBRID = 1;
    public static final int PURITY_LINE = 2;
    public static final int PURITY_PURE = 3;

    private FlyLineage() {
    }

    public static int generation(ItemStack stack) {
        Integer value = stack.get(FlyimpactComponents.GENERATION.get());
        return value == null ? 0 : value;
    }

    public static int purity(ItemStack stack) {
        Integer value = stack.get(FlyimpactComponents.PURITY.get());
        return value == null ? PURITY_WILD : value;
    }

    public static int childGeneration(ItemStack parent1, ItemStack parent2) {
        return Math.min(MAX_GENERATION, Math.max(generation(parent1), generation(parent2)) + 1);
    }

    public static ItemStack createOffspring(ItemStack parent1, ItemStack parent2, FlyType.Type resultType) {
        ItemStack child = FlyItem.withType(resultType);
        child.set(FlyimpactComponents.GENERATION.get(), childGeneration(parent1, parent2));
        child.set(FlyimpactComponents.PURITY.get(), childPurity(parent1, parent2, resultType));
        return child;
    }

    public static int childPurity(ItemStack parent1, ItemStack parent2, FlyType.Type resultType) {
        String childId = resultType.id();
        String firstId = FlyItem.getFlyType(parent1).id();
        String secondId = FlyItem.getFlyType(parent2).id();
        boolean mutated = !childId.equals(firstId) && !childId.equals(secondId);
        if (mutated || !firstId.equals(secondId)) {
            return PURITY_HYBRID;
        }
        return purify(purity(parent1), purity(parent2));
    }

    public static boolean sameVariety(ItemStack parent1, ItemStack parent2) {
        return FlyItem.getFlyType(parent1).id().equals(FlyItem.getFlyType(parent2).id());
    }

    public static MutableComponent generationLabel(int generation) {
        if (generation <= 0) {
            return Component.translatable("tooltip.flyimpact.generation_wild");
        }
        return Component.translatable("tooltip.flyimpact.generation", generation);
    }

    public static MutableComponent purityLabel(int purity) {
        String key = switch (purity) {
            case PURITY_HYBRID -> "tooltip.flyimpact.purity.hybrid";
            case PURITY_LINE -> "tooltip.flyimpact.purity.line";
            case PURITY_PURE -> "tooltip.flyimpact.purity.pure";
            default -> "tooltip.flyimpact.purity.wild";
        };
        return Component.translatable("tooltip.flyimpact.purity", Component.translatable(key));
    }

    public static ChatFormatting purityColor(int purity) {
        return switch (purity) {
            case PURITY_HYBRID -> ChatFormatting.YELLOW;
            case PURITY_LINE -> ChatFormatting.GREEN;
            case PURITY_PURE -> ChatFormatting.GOLD;
            default -> ChatFormatting.DARK_GRAY;
        };
    }

    private static int purify(int first, int second) {
        int lowest = Math.min(first, second);
        int highest = Math.max(first, second);
        if (lowest >= PURITY_LINE) {
            return PURITY_PURE;
        }
        if (highest >= PURITY_LINE || lowest >= PURITY_HYBRID) {
            return PURITY_LINE;
        }
        return PURITY_LINE;
    }
}

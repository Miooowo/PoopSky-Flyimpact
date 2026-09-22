package com.altnoir.flyimpact.breeding;

import com.altnoir.poopsky.impl.PoTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public final class SimpleBreedingChestBonus {
    private SimpleBreedingChestBonus() {
    }

    public static boolean isAccelerator(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(PoTags.Items.BREEDING_CHEST_ACCELERATOR)) {
            return true;
        }
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(PoTags.Blocks.BREEDING_CHEST_ACCELERATOR);
    }

    public static boolean isParallelism(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(PoTags.Items.BREEDING_CHEST_PARALLELISM)) {
            return true;
        }
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(PoTags.Blocks.BREEDING_CHEST_PARALLELISM);
    }
}

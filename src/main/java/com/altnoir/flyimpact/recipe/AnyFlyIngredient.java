package com.altnoir.flyimpact.recipe;

import com.altnoir.flyimpact.item.FlyimpactTags;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class AnyFlyIngredient implements ICustomIngredient {
    public static final AnyFlyIngredient INSTANCE = new AnyFlyIngredient();

    private AnyFlyIngredient() {
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.is(FlyimpactTags.FLIES) || stack.getItem() instanceof FlyItem;
    }

    @Override
    public Stream<ItemStack> getItems() {
        List<ItemStack> stacks = new ArrayList<>();
        Set<Item> expandedFlyItems = new LinkedHashSet<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(FlyimpactTags.FLIES)) {
            Item item = holder.value();
            if (item instanceof FlyItem) {
                if (expandedFlyItems.add(item)) {
                    addAllFlyTypes(stacks);
                }
            } else {
                stacks.add(new ItemStack(item));
            }
        }
        if (stacks.isEmpty()) {
            addAllFlyTypes(stacks);
        }
        return stacks.stream();
    }

    private static void addAllFlyTypes(List<ItemStack> stacks) {
        for (String type : FlyType.getAll().keySet()) {
            stacks.add(FlyItem.withType(type));
        }
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return FlyimpactRecipes.ANY_FLY.get();
    }
}

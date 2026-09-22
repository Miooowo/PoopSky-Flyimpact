package com.altnoir.flyimpact.recipe;

import com.altnoir.flyimpact.item.FlyimpactItems;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.init.FlyTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public class HybridizationGuideRecipe extends CustomRecipe {
    public HybridizationGuideRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!ModList.get().isLoaded("patchouli")) {
            return false;
        }
        boolean book = false;
        boolean maggot = false;
        boolean normalFly = false;
        int filled = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            filled++;
            if (stack.is(Items.BOOK) && !book) {
                book = true;
            } else if (isItem(stack, "maggots_seeds") && !maggot) {
                maggot = true;
            } else if (isItem(stack, "fly")
                    && FlyTypes.NORMAL.id().equals(FlyItem.getFlyType(stack).id())
                    && !normalFly) {
                normalFly = true;
            } else {
                return false;
            }
        }
        return filled == 3 && book && maggot && normalFly;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(FlyimpactItems.HYBRIDIZATION_GUIDE.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(FlyimpactItems.HYBRIDIZATION_GUIDE.get());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(Items.BOOK));
        ingredients.add(Ingredient.of(FlyItem.withType(FlyTypes.NORMAL.id())));
        ingredients.add(Ingredient.of(poopskyItem("maggots_seeds")));
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FlyimpactRecipes.HYBRIDIZATION_GUIDE.get();
    }

    private static boolean isItem(ItemStack stack, String path) {
        return stack.is(poopskyItem(path));
    }

    private static Item poopskyItem(String path) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("poopsky", path));
    }
}

package com.altnoir.flyimpact.recipe;

import com.altnoir.flyimpact.Flyimpact;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlyimpactRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Flyimpact.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<HybridizationGuideRecipe>> HYBRIDIZATION_GUIDE =
            SERIALIZERS.register("hybridization_guide", () -> new SimpleCraftingRecipeSerializer<>(HybridizationGuideRecipe::new));

    private FlyimpactRecipes() {
    }

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }
}

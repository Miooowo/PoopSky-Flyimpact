package com.altnoir.flyimpact.recipe;

import com.altnoir.flyimpact.Flyimpact;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class FlyimpactRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Flyimpact.MOD_ID);
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, Flyimpact.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<HybridizationGuideRecipe>> HYBRIDIZATION_GUIDE =
            SERIALIZERS.register("hybridization_guide", () -> new SimpleCraftingRecipeSerializer<>(HybridizationGuideRecipe::new));
    public static final DeferredHolder<IngredientType<?>, IngredientType<AnyFlyIngredient>> ANY_FLY =
            INGREDIENT_TYPES.register("any_fly", () -> new IngredientType<>(MapCodec.unit(AnyFlyIngredient.INSTANCE)));

    private FlyimpactRecipes() {
    }

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
        INGREDIENT_TYPES.register(modEventBus);
    }
}

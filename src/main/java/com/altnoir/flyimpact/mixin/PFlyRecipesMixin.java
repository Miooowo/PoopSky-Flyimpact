package com.altnoir.flyimpact.mixin;

import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.recipe.BreedingChestRecipe;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import com.altnoir.poopsky.init.PoRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Mixin(PFlyRecipes.class)
public abstract class PFlyRecipesMixin {
    @Inject(method = "tryMutate", at = @At("HEAD"), cancellable = true)
    private static void flyimpact$rollAllMatchingRecipes(
            Level level,
            FlyType.Type parent1,
            FlyType.Type parent2,
            CallbackInfoReturnable<PFlyRecipes.MutationResult> cir) {
        if (level == null) {
            return;
        }

        List<BreedingChestRecipe> matching = new ArrayList<>();
        for (RecipeHolder<BreedingChestRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(PoRecipes.BREEDING_CHEST.type().get())) {
            BreedingChestRecipe recipe = holder.value();
            if (recipe.matches(parent1.id(), parent2.id())) {
                matching.add(recipe);
            }
        }
        if (matching.isEmpty()) {
            return;
        }

        matching.sort(Comparator.comparing(BreedingChestRecipe::result));
        Random random = new Random();
        float roll = random.nextFloat();
        float accumulated = 0.0F;
        for (BreedingChestRecipe recipe : matching) {
            accumulated += recipe.chance();
            if (roll < accumulated) {
                cir.setReturnValue(new PFlyRecipes.MutationResult(FlyType.byId(recipe.result()), true));
                return;
            }
        }
        cir.setReturnValue(new PFlyRecipes.MutationResult(
                random.nextBoolean() ? parent1 : parent2, false));
    }
}

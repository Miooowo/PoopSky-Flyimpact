package com.altnoir.flyimpact.mixin;

import com.altnoir.poopsky.compat.jei.BreedingChestRecipeCategory;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.BreedingChestRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreedingChestRecipeCategory.class)
public abstract class BreedingChestRecipeCategoryMixin {
    @Inject(method = "setRecipe", at = @At("HEAD"), cancellable = true)
    private void flyimpact$addLineageHint(IRecipeLayoutBuilder builder, RecipeHolder<BreedingChestRecipe> recipeHolder, IFocusGroup focuses, CallbackInfo ci) {
        BreedingChestRecipe recipe = recipeHolder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStack(FlyItem.withType(FlyType.byId(recipe.parent1())));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                .addItemStack(FlyItem.withType(FlyType.byId(recipe.parent2())));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 56, 10)
                .addItemStack(FlyItem.withType(FlyType.byId(recipe.result())))
                .addRichTooltipCallback((view, tooltip) -> {
                    float percent = recipe.chance() * 100.0F;
                    String chance = percent < 1.0F ? "<1" : String.format("%.2f", percent).replaceAll("\\.?0+$", "");
                    tooltip.add(Component.translatable("jei.poopsky.breeding_chest_chance", chance).withStyle(ChatFormatting.GOLD));
                    tooltip.add(Component.translatable("jei.flyimpact.breeding_lineage").withStyle(ChatFormatting.GRAY));
                });
        ci.cancel();
    }
}

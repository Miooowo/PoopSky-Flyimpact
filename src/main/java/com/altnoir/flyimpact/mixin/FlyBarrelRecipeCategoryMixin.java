package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.poopsky.compat.jei.FlyBarrelRecipeCategory;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.FlyBarrelRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyBarrelRecipeCategory.class)
public abstract class FlyBarrelRecipeCategoryMixin {
    @Unique
    private static final int FLYIMPACT_OUTPUT_X = 56;
    @Unique
    private static final int FLYIMPACT_ALT_OUTPUT_X = 74;

    @Shadow
    @Final
    private IDrawable slot;

    @Inject(method = "getWidth", at = @At("RETURN"), cancellable = true)
    private void flyimpact$extraWidth(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + 18);
    }

    @Inject(method = "getHeight", at = @At("RETURN"), cancellable = true)
    private void flyimpact$extraHeight(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + 10);
    }

    @Inject(method = "setRecipe", at = @At("HEAD"), cancellable = true)
    private void flyimpact$pumpkinOutputs(IRecipeLayoutBuilder builder, RecipeHolder<FlyBarrelRecipe> recipeHolder, IFocusGroup focuses, CallbackInfo ci) {
        FlyBarrelRecipe recipe = recipeHolder.value();
        if (!Flyimpact.FLY_TYPE_PUMPKIN.equals(recipe.flyTypeId())) {
            return;
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStack(FlyItem.withType(FlyType.byId(recipe.flyTypeId())));
        builder.addSlot(RecipeIngredientRole.OUTPUT, FLYIMPACT_OUTPUT_X, 1)
                .addItemStack(recipe.resultStack())
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(
                        Component.translatable("jei.flyimpact.fly_barrel_chance", 75).withStyle(ChatFormatting.GOLD)));
        builder.addSlot(RecipeIngredientRole.OUTPUT, FLYIMPACT_ALT_OUTPUT_X, 1)
                .addItemStack(new ItemStack(Items.CARVED_PUMPKIN))
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(
                        Component.translatable("jei.flyimpact.fly_barrel_chance", 25).withStyle(ChatFormatting.GOLD)));
        ci.cancel();
    }

    @Inject(method = "draw", at = @At("TAIL"))
    private void flyimpact$drawPumpkinChance(RecipeHolder<FlyBarrelRecipe> recipeHolder, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        if (!Flyimpact.FLY_TYPE_PUMPKIN.equals(recipeHolder.value().flyTypeId())) {
            return;
        }
        this.slot.draw(graphics, FLYIMPACT_ALT_OUTPUT_X - 1, 0);
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "75%", FLYIMPACT_OUTPUT_X, 19, 0xFFAA00, false);
        graphics.drawString(font, "25%", FLYIMPACT_ALT_OUTPUT_X, 19, 0xFFAA00, false);
    }
}

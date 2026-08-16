package com.altnoir.flyimpact.compat.jei;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.poopsky.compat.jei.BreedingChestRecipeCategory;
import com.altnoir.poopsky.content.item.p.FlyItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class FlyimpactJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "jei");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new BreedingChestTransferHandler(registration.getTransferHelper()),
                BreedingChestRecipeCategory.TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_BLAZE),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.blaze"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_WHEAT),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.wheat"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_PUMPKIN),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.pumpkin"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_FAIRY),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.fairy"));
    }
}

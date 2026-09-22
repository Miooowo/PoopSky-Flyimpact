package com.altnoir.flyimpact.compat.jei;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.block.FlyimpactBlocks;
import com.altnoir.flyimpact.client.TieredFlyBarrelScreen;
import com.altnoir.flyimpact.item.FlyimpactItems;
import com.altnoir.poopsky.client.inventory.FlyBarrelScreen;
import com.altnoir.poopsky.compat.jei.BreedingChestRecipeCategory;
import com.altnoir.poopsky.compat.jei.FlyBarrelRecipeCategory;
import com.altnoir.poopsky.content.item.p.FlyItem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class FlyimpactJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(TieredFlyBarrelScreen.class, new FlyBarrelJeiGuiHandler<>());
        registration.addGuiContainerHandler(FlyBarrelScreen.class, new FlyBarrelJeiGuiHandler<>());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new BreedingChestTransferHandler(registration.getTransferHelper()),
                BreedingChestRecipeCategory.TYPE);
        registration.addRecipeTransferHandler(
                new SimpleBreedingChestTransferHandler(registration.getTransferHelper()),
                BreedingChestRecipeCategory.TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(FlyimpactBlocks.SIMPLE_BREEDING_CHEST_ITEM.get()),
                BreedingChestRecipeCategory.TYPE);
        registration.addRecipeCatalyst(
                new ItemStack(FlyimpactBlocks.COPPER_FLY_BARREL_ITEM.get()),
                FlyBarrelRecipeCategory.TYPE);
        registration.addRecipeCatalyst(
                new ItemStack(FlyimpactBlocks.IRON_FLY_BARREL_ITEM.get()),
                FlyBarrelRecipeCategory.TYPE);
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
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_ALTNOIR),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.altnoir"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_STEVE),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.steve"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_CHILI),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.chili"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_COAL),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.coal"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_DIRT),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.dirt"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_GRASS_BLOCK),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.grass_block"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_EGG),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.egg"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_CHARCOAL),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.charcoal"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_ENDER_EYE),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.ender_eye"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_GHAST_TEAR),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.ghast_tear"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_END_CRYSTAL),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.end_crystal"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_SOUL_SOIL),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.soul_soil"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_NETHER_STAR),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.nether_star"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_SALTPETER),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.saltpeter"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_SNOWBALL),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.snowball"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_POOLIME),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.poolime"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_GRAVEL),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.gravel"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_GOLDEN_POOP),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.golden_poop"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_UREA),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.urea"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_ROUNDWORM),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.roundworm"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_COBBLESTONE),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.cobblestone"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_CARROT),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.carrot"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_POTATO),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.potato"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_BEETROOT),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.beetroot"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_KELP),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.kelp"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_COD),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.cod"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_SALMON),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.salmon"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_SAND),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.sand"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_SOUL_SAND),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.soul_sand"));
        registration.addIngredientInfo(
                FlyItem.withType(Flyimpact.FLY_TYPE_NETHER_WART),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.fly_desc.nether_wart"));
        registration.addIngredientInfo(
                new ItemStack(FlyimpactItems.COOKED_FLY.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.cooked_fly.info"));
        if (ModList.get().isLoaded("farmersdelight")) {
            registration.addIngredientInfo(
                    FlyItem.withType(Flyimpact.FLY_TYPE_RICH_SOIL),
                    VanillaTypes.ITEM_STACK,
                    Component.translatable("jei.flyimpact.fly_desc.rich_soil"));
        }
        registration.addIngredientInfo(
                new ItemStack(FlyimpactBlocks.SIMPLE_BREEDING_CHEST_ITEM.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.flyimpact.simple_breeding_chest.info"));
        if (ModList.get().isLoaded("patchouli")) {
            registration.addIngredientInfo(
                    new ItemStack(FlyimpactItems.HYBRIDIZATION_GUIDE.get()),
                    VanillaTypes.ITEM_STACK,
                    Component.translatable("tooltip.flyimpact.hybridization_guide"));
        }
    }
}

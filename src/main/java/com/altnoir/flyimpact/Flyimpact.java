package com.altnoir.flyimpact;

import com.altnoir.flyimpact.barrel.FlyBarrelUpgradeAccess;
import com.altnoir.flyimpact.block.FlyimpactBlocks;
import com.altnoir.flyimpact.block.entity.FlyimpactBlockEntities;
import com.altnoir.flyimpact.block.entity.SimpleBreedingChestBlockEntity;
import com.altnoir.flyimpact.compat.sophisticatedbackpacks.BackpackCompat;
import com.altnoir.flyimpact.event.FlyFuel;
import com.altnoir.flyimpact.event.GiveHybridizationGuide;
import com.altnoir.flyimpact.item.FlyimpactComponents;
import com.altnoir.flyimpact.item.FlyimpactItems;
import com.altnoir.flyimpact.menu.FlyimpactMenus;
import com.altnoir.flyimpact.network.FlyimpactNetwork;
import com.altnoir.flyimpact.recipe.FlyimpactRecipes;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(Flyimpact.MOD_ID)
public class Flyimpact {
    public static final String MOD_ID = "flyimpact";
    public static final String FLY_TYPE_LEATHER = "leather";
    public static final String FLY_TYPE_ROTTEN_FLESH = "rotten_flesh";
    public static final String FLY_TYPE_BLAZE = "blaze";
    public static final String FLY_TYPE_WHEAT = "wheat";
    public static final String FLY_TYPE_PUMPKIN = "pumpkin";
    public static final String FLY_TYPE_FAIRY = "fairy";
    public static final String FLY_TYPE_NORMAL = "normal";
    public static final String FLY_TYPE_OBSIDIAN = "obsidian";
    public static final String FLY_TYPE_ALTNOIR = "altnoir";
    public static final String FLY_TYPE_DIORITE = "diorite";
    public static final String FLY_TYPE_ANDESITE = "andesite";
    public static final String FLY_TYPE_GRANITE = "granite";
    public static final String FLY_TYPE_CHILI = "chili";
    public static final String FLY_TYPE_SPICY_POOP = "spicy_poop";
    public static final String FLY_TYPE_COAL = "coal";
    public static final String FLY_TYPE_GLOWSTONE = "glowstone";
    public static final String FLY_TYPE_STEVE = "steve";
    public static final String FLY_TYPE_DIRT = "dirt";
    public static final String FLY_TYPE_GRASS_BLOCK = "grass_block";
    public static final String FLY_TYPE_RICH_SOIL = "rich_soil";
    public static final String FLY_TYPE_EGG = "egg";
    public static final String FLY_TYPE_CHARCOAL = "charcoal";
    public static final String FLY_TYPE_ENDER_EYE = "ender_eye";
    public static final String FLY_TYPE_GHAST_TEAR = "ghast_tear";
    public static final String FLY_TYPE_END_CRYSTAL = "end_crystal";
    public static final String FLY_TYPE_SOUL_SOIL = "soul_soil";
    public static final String FLY_TYPE_NETHER_STAR = "nether_star";
    public static final String FLY_TYPE_SALTPETER = "saltpeter";
    public static final String FLY_TYPE_SNOWBALL = "snowball";
    public static final String FLY_TYPE_POOLIME = "poolime";
    public static final String FLY_TYPE_GRAVEL = "gravel";
    public static final String FLY_TYPE_GOLDEN_POOP = "golden_poop";
    public static final String FLY_TYPE_UREA = "urea";
    public static final String FLY_TYPE_ROUNDWORM = "roundworm";
    public static final String FLY_TYPE_COBBLESTONE = "cobblestone";
    public static final String FLY_TYPE_CARROT = "carrot";
    public static final String FLY_TYPE_POTATO = "potato";
    public static final String FLY_TYPE_BEETROOT = "beetroot";
    public static final String FLY_TYPE_KELP = "kelp";
    public static final String FLY_TYPE_COD = "cod";
    public static final String FLY_TYPE_SALMON = "salmon";
    public static final String FLY_TYPE_SAND = "sand";
    public static final String FLY_TYPE_SOUL_SAND = "soul_sand";
    public static final String FLY_TYPE_NETHER_WART = "nether_wart";
    public static final List<String> EXTRA_FLY_TYPES = List.of(
            FLY_TYPE_LEATHER,
            FLY_TYPE_ROTTEN_FLESH,
            FLY_TYPE_BLAZE,
            FLY_TYPE_WHEAT,
            FLY_TYPE_PUMPKIN,
            FLY_TYPE_FAIRY,
            FLY_TYPE_OBSIDIAN,
            FLY_TYPE_ALTNOIR,
            FLY_TYPE_DIORITE,
            FLY_TYPE_ANDESITE,
            FLY_TYPE_GRANITE,
            FLY_TYPE_CHILI,
            FLY_TYPE_SPICY_POOP,
            FLY_TYPE_COAL,
            FLY_TYPE_STEVE,
            FLY_TYPE_DIRT,
            FLY_TYPE_GRASS_BLOCK,
            FLY_TYPE_RICH_SOIL,
            FLY_TYPE_EGG,
            FLY_TYPE_CHARCOAL,
            FLY_TYPE_ENDER_EYE,
            FLY_TYPE_GHAST_TEAR,
            FLY_TYPE_END_CRYSTAL,
            FLY_TYPE_SOUL_SOIL,
            FLY_TYPE_NETHER_STAR,
            FLY_TYPE_SALTPETER,
            FLY_TYPE_SNOWBALL,
            FLY_TYPE_POOLIME,
            FLY_TYPE_GRAVEL,
            FLY_TYPE_GOLDEN_POOP,
            FLY_TYPE_UREA,
            FLY_TYPE_ROUNDWORM,
            FLY_TYPE_COBBLESTONE,
            FLY_TYPE_CARROT,
            FLY_TYPE_POTATO,
            FLY_TYPE_BEETROOT,
            FLY_TYPE_KELP,
            FLY_TYPE_COD,
            FLY_TYPE_SALMON,
            FLY_TYPE_SAND,
            FLY_TYPE_SOUL_SAND,
            FLY_TYPE_NETHER_WART
    );
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public Flyimpact(IEventBus modEventBus, ModContainer modContainer) {
        FlyimpactBlocks.register(modEventBus);
        FlyimpactBlockEntities.register(modEventBus);
        FlyimpactItems.register(modEventBus);
        FlyimpactComponents.register(modEventBus);
        FlyimpactMenus.register(modEventBus);
        FlyimpactRecipes.register(modEventBus);
        FlyimpactNetwork.register(modEventBus);
        if (ModList.get().isLoaded("sophisticatedbackpacks")) {
            BackpackCompat.register(modEventBus);
        }
        modEventBus.addListener(Flyimpact::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(FlyTypeInjector::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(FlyTypeInjector::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(FlyimpactCommands::register);
        NeoForge.EVENT_BUS.addListener(FlyFeeding::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(FlyRelease::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(AltnoirBreeding::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(FlyFuel::onFurnaceFuel);
        NeoForge.EVENT_BUS.addListener(FlyFuel::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(GiveHybridizationGuide::onPlayerLoggedIn);
        LOGGER.info("PoopSky: Flyimpact loaded");
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, direction) -> {
                    if (!(blockEntity instanceof FlyBarrelBlockEntity barrel)) {
                        return null;
                    }
                    if (direction == null || direction == Direction.DOWN) {
                        return barrel.getBottomHandler();
                    }
                    return barrel.getTopSideHandler();
                },
                FlyimpactBlocks.COPPER_FLY_BARREL.get(),
                FlyimpactBlocks.IRON_FLY_BARREL.get());
        Block woodFlyBarrel = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("poopsky", "fly_barrel"));
        if (woodFlyBarrel != Blocks.AIR) {
            event.registerBlock(
                    Capabilities.EnergyStorage.BLOCK,
                    (level, pos, state, blockEntity, direction) ->
                            blockEntity instanceof FlyBarrelUpgradeAccess access ? access.flyimpact$energy() : null,
                    woodFlyBarrel,
                    FlyimpactBlocks.COPPER_FLY_BARREL.get(),
                    FlyimpactBlocks.IRON_FLY_BARREL.get());
            event.registerBlock(
                    Capabilities.FluidHandler.BLOCK,
                    (level, pos, state, blockEntity, direction) ->
                            blockEntity instanceof FlyBarrelUpgradeAccess access ? access.flyimpact$fluid() : null,
                    woodFlyBarrel,
                    FlyimpactBlocks.COPPER_FLY_BARREL.get(),
                    FlyimpactBlocks.IRON_FLY_BARREL.get());
        } else {
            event.registerBlock(
                    Capabilities.EnergyStorage.BLOCK,
                    (level, pos, state, blockEntity, direction) ->
                            blockEntity instanceof FlyBarrelUpgradeAccess access ? access.flyimpact$energy() : null,
                    FlyimpactBlocks.COPPER_FLY_BARREL.get(),
                    FlyimpactBlocks.IRON_FLY_BARREL.get());
            event.registerBlock(
                    Capabilities.FluidHandler.BLOCK,
                    (level, pos, state, blockEntity, direction) ->
                            blockEntity instanceof FlyBarrelUpgradeAccess access ? access.flyimpact$fluid() : null,
                    FlyimpactBlocks.COPPER_FLY_BARREL.get(),
                    FlyimpactBlocks.IRON_FLY_BARREL.get());
        }
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FlyimpactBlockEntities.SIMPLE_BREEDING_CHEST.get(),
                (SimpleBreedingChestBlockEntity be, Direction direction) -> {
                    if (direction == null || direction == Direction.DOWN) {
                        return be.getBottomHandler();
                    }
                    return be.getTopSideHandler();
                });
    }
}

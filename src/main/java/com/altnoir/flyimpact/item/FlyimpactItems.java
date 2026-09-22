package com.altnoir.flyimpact.item;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.block.FlyimpactBlocks;
import com.altnoir.flyimpact.compat.sophisticatedbackpacks.BackpackCompat;
import com.altnoir.flyimpact.item.UpgradeModuleItem.Kind;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlyimpactItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Flyimpact.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Flyimpact.MOD_ID);

    public static final DeferredHolder<Item, UpgradeModuleItem> POOP_YIELD_UPGRADE = yieldModule("poop_yield_upgrade", 25);
    public static final DeferredHolder<Item, UpgradeModuleItem> COPPER_YIELD_UPGRADE = yieldModule("copper_yield_upgrade", 50);
    public static final DeferredHolder<Item, UpgradeModuleItem> IRON_YIELD_UPGRADE = yieldModule("iron_yield_upgrade", 100);
    public static final DeferredHolder<Item, UpgradeModuleItem> GOLD_YIELD_UPGRADE = yieldModule("gold_yield_upgrade", 150);
    public static final DeferredHolder<Item, UpgradeModuleItem> EMERALD_YIELD_UPGRADE = yieldModule("emerald_yield_upgrade", 200);
    public static final DeferredHolder<Item, UpgradeModuleItem> DIAMOND_YIELD_UPGRADE = yieldModule("diamond_yield_upgrade", 300);
    public static final DeferredHolder<Item, UpgradeModuleItem> OBSIDIAN_YIELD_UPGRADE = yieldModule("obsidian_yield_upgrade", 400);

    public static final DeferredHolder<Item, UpgradeModuleItem> POOP_SPEED_UPGRADE = speed("poop_speed_upgrade", 25);
    public static final DeferredHolder<Item, UpgradeModuleItem> COPPER_SPEED_UPGRADE = speed("copper_speed_upgrade", 50);
    public static final DeferredHolder<Item, UpgradeModuleItem> IRON_SPEED_UPGRADE = speed("iron_speed_upgrade", 100);
    public static final DeferredHolder<Item, UpgradeModuleItem> GOLD_SPEED_UPGRADE = speed("gold_speed_upgrade", 150);
    public static final DeferredHolder<Item, UpgradeModuleItem> EMERALD_SPEED_UPGRADE = speed("emerald_speed_upgrade", 200);
    public static final DeferredHolder<Item, UpgradeModuleItem> DIAMOND_SPEED_UPGRADE = speed("diamond_speed_upgrade", 300);
    public static final DeferredHolder<Item, UpgradeModuleItem> OBSIDIAN_SPEED_UPGRADE = speed("obsidian_speed_upgrade", 400);
    public static final DeferredHolder<Item, UpgradeModuleItem> CONTAINER_TRANSFER_UPGRADE =
            transfer("container_transfer_upgrade", 1);
    public static final DeferredHolder<Item, UpgradeModuleItem> COPPER_CONTAINER_TRANSFER_UPGRADE =
            transfer("copper_container_transfer_upgrade", 8);
    public static final DeferredHolder<Item, UpgradeModuleItem> IRON_CONTAINER_TRANSFER_UPGRADE =
            transfer("iron_container_transfer_upgrade", 16);
    public static final DeferredHolder<Item, UpgradeModuleItem> GOLD_CONTAINER_TRANSFER_UPGRADE =
            transfer("gold_container_transfer_upgrade", 32);
    public static final DeferredHolder<Item, UpgradeModuleItem> DIAMOND_CONTAINER_TRANSFER_UPGRADE =
            transfer("diamond_container_transfer_upgrade", 64);
    public static final DeferredHolder<Item, UpgradeModuleItem> OBSIDIAN_CONTAINER_TRANSFER_UPGRADE =
            transfer("obsidian_container_transfer_upgrade", 128);
    public static final DeferredHolder<Item, UpgradeModuleItem> FLUID_STORAGE_UPGRADE =
            ITEMS.register("fluid_storage_upgrade", () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.FLUID, 0));
    public static final DeferredHolder<Item, UpgradeModuleItem> ENERGY_STORAGE_UPGRADE =
            ITEMS.register("energy_storage_upgrade", () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.ENERGY, 0));

    public static final DeferredHolder<Item, HybridizationGuideItem> HYBRIDIZATION_GUIDE =
            ITEMS.register("hybridization_guide", () -> new HybridizationGuideItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> COOKED_FLY = ITEMS.register("cooked_fly", () -> new Item(
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.3F)
                    .fast()
                    .build())));
    public static final DeferredHolder<Item, Item> END_CRYSTAL_SHARD = simple("end_crystal_shard");
    public static final DeferredHolder<Item, Item> NETHER_STAR_CRUMB = simple("nether_star_crumb");
    public static final DeferredHolder<Item, Item> NETHER_STAR_FRAGMENT = simple("nether_star_fragment");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.flyimpact"))
            .icon(() -> new ItemStack(POOP_YIELD_UPGRADE.get()))
            .displayItems((parameters, output) -> {
                if (ModList.get().isLoaded("patchouli")) {
                    output.accept(HYBRIDIZATION_GUIDE.get());
                }
                output.accept(COOKED_FLY.get());
                output.accept(END_CRYSTAL_SHARD.get());
                output.accept(NETHER_STAR_CRUMB.get());
                output.accept(NETHER_STAR_FRAGMENT.get());
                output.accept(FlyimpactBlocks.SIMPLE_BREEDING_CHEST_ITEM.get());
                output.accept(FlyimpactBlocks.COPPER_FLY_BARREL_ITEM.get());
                output.accept(FlyimpactBlocks.IRON_FLY_BARREL_ITEM.get());
                output.accept(POOP_YIELD_UPGRADE.get());
                output.accept(COPPER_YIELD_UPGRADE.get());
                output.accept(IRON_YIELD_UPGRADE.get());
                output.accept(GOLD_YIELD_UPGRADE.get());
                output.accept(EMERALD_YIELD_UPGRADE.get());
                output.accept(DIAMOND_YIELD_UPGRADE.get());
                output.accept(OBSIDIAN_YIELD_UPGRADE.get());
                output.accept(POOP_SPEED_UPGRADE.get());
                output.accept(COPPER_SPEED_UPGRADE.get());
                output.accept(IRON_SPEED_UPGRADE.get());
                output.accept(GOLD_SPEED_UPGRADE.get());
                output.accept(EMERALD_SPEED_UPGRADE.get());
                output.accept(DIAMOND_SPEED_UPGRADE.get());
                output.accept(OBSIDIAN_SPEED_UPGRADE.get());
                output.accept(CONTAINER_TRANSFER_UPGRADE.get());
                output.accept(COPPER_CONTAINER_TRANSFER_UPGRADE.get());
                output.accept(IRON_CONTAINER_TRANSFER_UPGRADE.get());
                output.accept(GOLD_CONTAINER_TRANSFER_UPGRADE.get());
                output.accept(DIAMOND_CONTAINER_TRANSFER_UPGRADE.get());
                output.accept(OBSIDIAN_CONTAINER_TRANSFER_UPGRADE.get());
                output.accept(FLUID_STORAGE_UPGRADE.get());
                output.accept(ENERGY_STORAGE_UPGRADE.get());
                if (ModList.get().isLoaded("sophisticatedbackpacks")) {
                    output.accept(BackpackCompat.FLY_BARREL_UPGRADE.get());
                }
            })
            .build());

    private FlyimpactItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);
    }

    private static DeferredHolder<Item, Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static DeferredHolder<Item, UpgradeModuleItem> yieldModule(String name, int percent) {
        return ITEMS.register(name, () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.YIELD, percent));
    }

    private static DeferredHolder<Item, UpgradeModuleItem> speed(String name, int percent) {
        return ITEMS.register(name, () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.SPEED, percent));
    }

    private static DeferredHolder<Item, UpgradeModuleItem> transfer(String name, int multiplier) {
        return ITEMS.register(name, () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.TRANSFER, multiplier));
    }
}

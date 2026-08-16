package com.altnoir.flyimpact.item;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.item.UpgradeModuleItem.Kind;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.flyimpact"))
            .icon(() -> new ItemStack(POOP_YIELD_UPGRADE.get()))
            .displayItems((parameters, output) -> {
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
            })
            .build());

    private FlyimpactItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);
    }

    private static DeferredHolder<Item, UpgradeModuleItem> yieldModule(String name, int percent) {
        return ITEMS.register(name, () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.YIELD, percent));
    }

    private static DeferredHolder<Item, UpgradeModuleItem> speed(String name, int percent) {
        return ITEMS.register(name, () -> new UpgradeModuleItem(new Item.Properties().stacksTo(1), Kind.SPEED, percent));
    }
}

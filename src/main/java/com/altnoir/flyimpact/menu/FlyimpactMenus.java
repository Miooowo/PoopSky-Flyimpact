package com.altnoir.flyimpact.menu;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.barrel.BarrelTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlyimpactMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Flyimpact.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<TieredFlyBarrelMenu>> COPPER_FLY_BARREL =
            MENUS.register("copper_fly_barrel", () -> new MenuType<>(
                    (id, inventory) -> new TieredFlyBarrelMenu(id, inventory, BarrelTier.COPPER),
                    FeatureFlags.VANILLA_SET));

    public static final DeferredHolder<MenuType<?>, MenuType<TieredFlyBarrelMenu>> IRON_FLY_BARREL =
            MENUS.register("iron_fly_barrel", () -> new MenuType<>(
                    (id, inventory) -> new TieredFlyBarrelMenu(id, inventory, BarrelTier.IRON),
                    FeatureFlags.VANILLA_SET));

    public static final DeferredHolder<MenuType<?>, MenuType<SimpleBreedingChestMenu>> SIMPLE_BREEDING_CHEST =
            MENUS.register("simple_breeding_chest", () -> new MenuType<>(
                    SimpleBreedingChestMenu::new,
                    FeatureFlags.VANILLA_SET));

    private FlyimpactMenus() {
    }

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}

package com.altnoir.flyimpact.compat.sophisticatedbackpacks;

import com.altnoir.flyimpact.Flyimpact;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeCountLimitConfig;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeGroup;

import javax.annotation.Nullable;

public final class BackpackCompat {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Flyimpact.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Flyimpact.MOD_ID);

    public static final DeferredHolder<Item, FlyBarrelUpgradeItem> FLY_BARREL_UPGRADE =
            ITEMS.register("fly_barrel_upgrade", FlyBarrelUpgradeItem::new);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> INVENTORY =
            DATA_COMPONENTS.register("fly_barrel_inventory", () -> DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .cacheEncoding()
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> PRODUCE_FINISH =
            DATA_COMPONENTS.register("fly_barrel_produce_finish", () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PRODUCE_TOTAL =
            DATA_COMPONENTS.register("fly_barrel_produce_total", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PRODUCING =
            DATA_COMPONENTS.register("fly_barrel_producing", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final UpgradeContainerType<FlyBarrelUpgradeWrapper, FlyBarrelUpgradeContainer> CONTAINER_TYPE =
            new UpgradeContainerType<>(FlyBarrelUpgradeContainer::new);

    public static final IUpgradeCountLimitConfig ONE_PER_STORAGE = new IUpgradeCountLimitConfig() {
        @Override
        public int getMaxUpgradesPerStorage(String storageType, @Nullable ResourceLocation upgradeRegistryName) {
            return 1;
        }

        @Override
        public int getMaxUpgradesInGroupPerStorage(String storageType, UpgradeGroup upgradeGroup) {
            return 1;
        }
    };

    private BackpackCompat() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        modEventBus.addListener(BackpackCompat::registerContainers);
        Flyimpact.LOGGER.info("Sophisticated Backpacks fly barrel upgrade enabled");
    }

    private static void registerContainers(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) {
            return;
        }
        UpgradeContainerRegistry.register(FLY_BARREL_UPGRADE.getId(), CONTAINER_TYPE);
    }
}

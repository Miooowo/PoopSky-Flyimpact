package com.altnoir.flyimpact;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlyimpactComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Flyimpact.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GENERATION =
            COMPONENTS.registerComponentType("generation", builder -> builder
                    .persistent(ExtraCodecs.intRange(0, 32))
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PURITY =
            COMPONENTS.registerComponentType("purity", builder -> builder
                    .persistent(ExtraCodecs.intRange(0, 3))
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    private FlyimpactComponents() {
    }

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}

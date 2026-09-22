package com.altnoir.flyimpact.block.entity;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.block.FlyimpactBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FlyimpactBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Flyimpact.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleBreedingChestBlockEntity>> SIMPLE_BREEDING_CHEST =
            BLOCK_ENTITIES.register("simple_breeding_chest", () ->
                    BlockEntityType.Builder.of(
                            SimpleBreedingChestBlockEntity::new,
                            FlyimpactBlocks.SIMPLE_BREEDING_CHEST.get()
                    ).build(null));

    private FlyimpactBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}

package com.altnoir.flyimpact.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;

@Mixin(FlyBarrelBlockEntity.class)
public interface FlyBarrelBlockEntityAccessor {
    @Invoker("createContainerProxy")
    Container flyimpact$createContainerProxy();

    @Invoker("syncToClient")
    void flyimpact$syncToClient();

    @Accessor("data")
    ContainerData flyimpact$data();
}

package com.altnoir.flyimpact.menu;

import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;

public interface FlyBarrelStorageMenu {
    boolean flyimpact$hasFluidUpgrade();

    boolean flyimpact$hasEnergyUpgrade();

    int flyimpact$energyStored();

    int flyimpact$energyCapacity();

    int flyimpact$fluidAmount();

    int flyimpact$fluidId();

    default int flyimpact$fluidCapacity() {
        return com.altnoir.flyimpact.barrel.BarrelTier.FLUID_CAPACITY;
    }

    void flyimpact$interactTank(net.minecraft.world.entity.player.Player player);

    default FluidStack flyimpact$fluidStack() {
        int amount = flyimpact$fluidAmount();
        int id = flyimpact$fluidId();
        if (amount <= 0 || id <= 0) {
            return FluidStack.EMPTY;
        }
        var fluid = BuiltInRegistries.FLUID.byId(id);
        if (fluid == Fluids.EMPTY) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(fluid, amount);
    }
}

package com.altnoir.flyimpact.barrel;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface FlyBarrelUpgradeAccess {
    int flyimpact$inputSlots();

    int flyimpact$inventorySlots();

    int flyimpact$upgradeSlots();

    ItemStack flyimpact$getUpgrade(int slot);

    void flyimpact$setUpgrade(int slot, ItemStack stack);

    IEnergyStorage flyimpact$energy();

    IFluidHandler flyimpact$fluid();

    ContainerData flyimpact$storageData();

    boolean flyimpact$interactTank(Player player);
}

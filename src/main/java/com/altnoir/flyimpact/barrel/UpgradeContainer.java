package com.altnoir.flyimpact.barrel;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpgradeContainer implements Container {
    private final FlyBarrelUpgradeAccess access;

    public UpgradeContainer(FlyBarrelUpgradeAccess access) {
        this.access = access;
    }

    @Override
    public int getContainerSize() {
        return this.access.flyimpact$upgradeSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int slot = 0; slot < getContainerSize(); slot++) {
            if (!this.access.flyimpact$getUpgrade(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < getContainerSize() ? this.access.flyimpact$getUpgrade(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = getItem(slot);
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack split = current.split(amount);
        this.access.flyimpact$setUpgrade(slot, current);
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack current = getItem(slot);
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.access.flyimpact$setUpgrade(slot, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < getContainerSize()) {
            this.access.flyimpact$setUpgrade(slot, stack);
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int slot = 0; slot < getContainerSize(); slot++) {
            this.access.flyimpact$setUpgrade(slot, ItemStack.EMPTY);
        }
    }
}

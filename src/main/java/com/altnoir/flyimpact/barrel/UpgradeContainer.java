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
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return access.flyimpact$getUpgrade().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? access.flyimpact$getUpgrade() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = access.flyimpact$getUpgrade();
        if (slot != 0 || current.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack split = current.split(amount);
        access.flyimpact$setUpgrade(current);
        return split;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack current = access.flyimpact$getUpgrade();
        access.flyimpact$setUpgrade(ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            access.flyimpact$setUpgrade(stack);
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
        access.flyimpact$setUpgrade(ItemStack.EMPTY);
    }
}

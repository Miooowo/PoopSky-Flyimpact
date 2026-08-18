package com.altnoir.flyimpact.barrel;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class UpgradeAwareContainer implements Container, FlyBarrelContainerAccess {
    private final Container inner;
    private final FlyBarrelUpgradeAccess access;

    public UpgradeAwareContainer(Container inner, FlyBarrelUpgradeAccess access) {
        this.inner = inner;
        this.access = access;
    }

    @Override
    public FlyBarrelUpgradeAccess flyimpact$upgradeAccess() {
        return access;
    }

    @Override
    public int getContainerSize() {
        return inner.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inner.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inner.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inner.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inner.setItem(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return inner.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        inner.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return inner.stillValid(player);
    }

    @Override
    public void clearContent() {
        inner.clearContent();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return inner.canPlaceItem(slot, stack);
    }

    @Override
    public void startOpen(Player player) {
        inner.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        inner.stopOpen(player);
    }
}

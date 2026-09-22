package com.altnoir.flyimpact.menu;

import com.altnoir.flyimpact.barrel.BarrelTier;
import com.altnoir.flyimpact.barrel.FlyBarrelContainerAccess;
import com.altnoir.flyimpact.barrel.UpgradeContainer;
import com.altnoir.flyimpact.item.UpgradeModuleItem;
import com.altnoir.flyimpact.upgrade.UpgradePanelLayout;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class TieredFlyBarrelMenu extends AbstractContainerMenu implements FlyBarrelStorageMenu {
    private final Container flyBarrel;
    private final ContainerData data;
    private final ContainerData storageData;
    private final BarrelTier tier;
    private final int playerStart;
    private final int playerEnd;
    private final int upgradeStart;

    public TieredFlyBarrelMenu(int containerId, Inventory playerInventory, BarrelTier tier) {
        this(typeFor(tier), containerId, playerInventory, new SimpleContainer(tier.inventorySlots), new SimpleContainerData(tier.inputSlots * 2), tier);
    }

    public TieredFlyBarrelMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, BarrelTier tier) {
        this(typeFor(tier), containerId, playerInventory, container, data, tier);
    }

    private TieredFlyBarrelMenu(
            MenuType<?> menuType,
            int containerId,
            Inventory playerInventory,
            Container container,
            ContainerData data,
            BarrelTier tier) {
        super(menuType, containerId);
        checkContainerSize(container, tier.inventorySlots);
        checkContainerDataCount(data, tier.inputSlots * 2);
        this.flyBarrel = container;
        this.data = data;
        this.tier = tier;
        this.playerStart = tier.inventorySlots;
        this.playerEnd = this.playerStart + 36;
        this.upgradeStart = this.playerEnd;
        container.startOpen(playerInventory.player);
        addDataSlots(data);
        if (container instanceof FlyBarrelContainerAccess access) {
            this.storageData = access.flyimpact$upgradeAccess().flyimpact$storageData();
        } else {
            this.storageData = new SimpleContainerData(BarrelTier.STORAGE_DATA_SIZE);
        }
        addDataSlots(this.storageData);

        for (int i = 0; i < tier.inputSlots; i++) {
            addSlot(new Slot(container, i, BarrelTier.INPUT_X, BarrelTier.INPUT_Y + i * BarrelTier.SLOT_SIZE) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return FlyItem.isFlyItem(stack);
                }
            });
        }
        int extraOffset = tier.guiOffset();
        for (int i = 0; i < tier.outputSlots; i++) {
            int row = i / BarrelTier.OUTPUTS_PER_ROW;
            int col = i % BarrelTier.OUTPUTS_PER_ROW;
            addSlot(new Slot(
                    container,
                    tier.inputSlots + i,
                    BarrelTier.OUTPUT_X + col * BarrelTier.SLOT_SIZE,
                    BarrelTier.OUTPUT_Y + row * BarrelTier.SLOT_SIZE) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        int playerY = BarrelTier.PLAYER_Y + extraOffset;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * BarrelTier.SLOT_SIZE, playerY + row * BarrelTier.SLOT_SIZE));
            }
        }
        int hotbarY = BarrelTier.HOTBAR_Y + extraOffset;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * BarrelTier.SLOT_SIZE, hotbarY));
        }

        Container upgradeContainer = container instanceof FlyBarrelContainerAccess access
                ? new UpgradeContainer(access.flyimpact$upgradeAccess())
                : new SimpleContainer(tier.upgradeSlots);
        for (int i = 0; i < tier.upgradeSlots; i++) {
            addSlot(new Slot(upgradeContainer, i, UpgradePanelLayout.SLOT_X, UpgradePanelLayout.slotY(i)) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return UpgradeModuleItem.isUpgrade(stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
    }

    public BarrelTier tier() {
        return this.tier;
    }

    public int getProgress() {
        return getProgress(0);
    }

    public int getMaxProgress() {
        return getMaxProgress(0);
    }

    public int getProgress(int inputSlot) {
        int index = inputSlot * 2;
        return index >= 0 && index < this.data.getCount() ? this.data.get(index) : 0;
    }

    public int getMaxProgress(int inputSlot) {
        int index = inputSlot * 2 + 1;
        return index >= 0 && index < this.data.getCount() ? this.data.get(index) : 0;
    }

    @Override
    public boolean flyimpact$hasFluidUpgrade() {
        for (int i = 0; i < this.tier.upgradeSlots; i++) {
            if (UpgradeModuleItem.hasFluid(upgradeInSlot(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean flyimpact$hasEnergyUpgrade() {
        for (int i = 0; i < this.tier.upgradeSlots; i++) {
            if (UpgradeModuleItem.hasEnergy(upgradeInSlot(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int flyimpact$energyStored() {
        return this.storageData.get(BarrelTier.STORAGE_ENERGY);
    }

    @Override
    public int flyimpact$energyCapacity() {
        int cap = this.storageData.get(BarrelTier.STORAGE_ENERGY_CAP);
        return cap > 0 ? cap : BarrelTier.ENERGY_CAPACITY;
    }

    @Override
    public int flyimpact$fluidAmount() {
        return this.storageData.get(BarrelTier.STORAGE_FLUID_AMOUNT);
    }

    @Override
    public int flyimpact$fluidId() {
        return this.storageData.get(BarrelTier.STORAGE_FLUID_ID);
    }

    @Override
    public void flyimpact$interactTank(Player player) {
        if (this.flyBarrel instanceof FlyBarrelContainerAccess access) {
            access.flyimpact$upgradeAccess().flyimpact$interactTank(player);
        }
    }

    public ItemStack upgradeInSlot(int index) {
        int slot = this.upgradeStart + index;
        return slot >= 0 && slot < this.slots.size() ? this.slots.get(slot).getItem() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();
        int upgradeEnd = this.upgradeStart + this.tier.upgradeSlots;
        if (index < this.playerStart) {
            if (!moveItemStackTo(source, this.playerStart, this.playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= this.upgradeStart && index < upgradeEnd) {
            if (!moveItemStackTo(source, this.playerStart, this.playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (UpgradeModuleItem.isUpgrade(source)) {
            if (!moveItemStackTo(source, this.upgradeStart, upgradeEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else if (FlyItem.isFlyItem(source)) {
            if (!moveItemStackTo(source, 0, this.tier.inputSlots, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < this.playerStart + 27) {
            if (!moveItemStackTo(source, this.playerStart + 27, this.playerEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, this.playerStart, this.playerStart + 27, false)) {
            return ItemStack.EMPTY;
        }
        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (source.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, source);
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.flyBarrel.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.flyBarrel.stopOpen(player);
    }

    private static MenuType<?> typeFor(BarrelTier tier) {
        return tier == BarrelTier.IRON ? FlyimpactMenus.IRON_FLY_BARREL.get() : FlyimpactMenus.COPPER_FLY_BARREL.get();
    }
}

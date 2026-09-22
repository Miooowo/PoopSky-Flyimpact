package com.altnoir.flyimpact.menu;

import com.altnoir.flyimpact.breeding.SimpleBreedingChestBonus;
import com.altnoir.flyimpact.breeding.SimpleBreedingChestSlots;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.impl.PoTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class SimpleBreedingChestMenu extends AbstractContainerMenu {
    private final Container breedingChest;
    private final ContainerData data;

    public SimpleBreedingChestMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SimpleBreedingChestSlots.TOTAL), new SimpleContainerData(SimpleBreedingChestSlots.DATA_COUNT));
    }

    public SimpleBreedingChestMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(FlyimpactMenus.SIMPLE_BREEDING_CHEST.get(), containerId);
        checkContainerSize(container, SimpleBreedingChestSlots.TOTAL);
        checkContainerDataCount(data, SimpleBreedingChestSlots.DATA_COUNT);
        this.breedingChest = container;
        this.data = data;
        container.startOpen(playerInventory.player);
        addDataSlots(data);

        addSlot(new Slot(container, SimpleBreedingChestSlots.FECES, SimpleBreedingChestSlots.FECES_X, SimpleBreedingChestSlots.FECES_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PoTags.Items.POOPS);
            }

            @Override
            public int getMaxStackSize() {
                return SimpleBreedingChestSlots.FECES_LIMIT;
            }
        });
        addSlot(new Slot(container, SimpleBreedingChestSlots.FLY_1, SimpleBreedingChestSlots.FLY_1_X, SimpleBreedingChestSlots.FLY_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FlyItem.isFlyItem(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(container, SimpleBreedingChestSlots.FLY_2, SimpleBreedingChestSlots.FLY_2_X, SimpleBreedingChestSlots.FLY_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FlyItem.isFlyItem(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        for (int i = 0; i < SimpleBreedingChestSlots.OUTPUT_COUNT; i++) {
            addSlot(new Slot(
                    container,
                    SimpleBreedingChestSlots.OUTPUT_1 + i,
                    SimpleBreedingChestSlots.OUTPUT_X + i * 18,
                    SimpleBreedingChestSlots.OUTPUT_Y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }
        for (int i = 0; i < SimpleBreedingChestSlots.ACCEL_COUNT; i++) {
            addSlot(new Slot(
                    container,
                    SimpleBreedingChestSlots.ACCEL_START + i,
                    SimpleBreedingChestSlots.accelX(i),
                    SimpleBreedingChestSlots.EXTRA_SLOT_Y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return SimpleBreedingChestBonus.isAccelerator(stack);
                }
            });
        }
        for (int i = 0; i < SimpleBreedingChestSlots.YIELD_COUNT; i++) {
            addSlot(new Slot(
                    container,
                    SimpleBreedingChestSlots.YIELD_START + i,
                    SimpleBreedingChestSlots.yieldX(i),
                    SimpleBreedingChestSlots.EXTRA_SLOT_Y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return SimpleBreedingChestBonus.isParallelism(stack);
                }
            });
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, SimpleBreedingChestSlots.PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, SimpleBreedingChestSlots.HOTBAR_Y));
        }
    }

    public int getProgress() {
        return data.get(SimpleBreedingChestSlots.DATA_PROGRESS);
    }

    public int getMaxProgress() {
        return data.get(SimpleBreedingChestSlots.DATA_INTERVAL);
    }

    public int getPoopBonus() {
        return data.get(SimpleBreedingChestSlots.DATA_POOP);
    }

    public int getMaggotsBonus() {
        return data.get(SimpleBreedingChestSlots.DATA_MAGGOTS);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack source = slot.getItem();
        copy = source.copy();

        int playerStart = SimpleBreedingChestSlots.TOTAL;
        int playerEnd = playerStart + 36;
        int hotbarStart = playerStart + 27;

        if (index < SimpleBreedingChestSlots.TOTAL) {
            if (!moveItemStackTo(source, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (SimpleBreedingChestBonus.isAccelerator(source)) {
            if (!moveItemStackTo(
                    source,
                    SimpleBreedingChestSlots.ACCEL_START,
                    SimpleBreedingChestSlots.ACCEL_START + SimpleBreedingChestSlots.ACCEL_COUNT,
                    false)) {
                return ItemStack.EMPTY;
            }
        } else if (SimpleBreedingChestBonus.isParallelism(source)) {
            if (!moveItemStackTo(
                    source,
                    SimpleBreedingChestSlots.YIELD_START,
                    SimpleBreedingChestSlots.YIELD_START + SimpleBreedingChestSlots.YIELD_COUNT,
                    false)) {
                return ItemStack.EMPTY;
            }
        } else if (source.is(PoTags.Items.POOPS)) {
            if (!moveItemStackTo(source, SimpleBreedingChestSlots.FECES, SimpleBreedingChestSlots.FECES + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (FlyItem.isFlyItem(source)) {
            if (!moveItemStackTo(source, SimpleBreedingChestSlots.FLY_1, SimpleBreedingChestSlots.FLY_2 + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < hotbarStart) {
            if (!moveItemStackTo(source, hotbarStart, playerEnd, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(source, playerStart, hotbarStart, false)) {
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
        return breedingChest.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        breedingChest.stopOpen(player);
    }
}

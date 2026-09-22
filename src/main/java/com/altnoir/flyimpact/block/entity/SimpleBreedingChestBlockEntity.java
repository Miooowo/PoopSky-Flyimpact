package com.altnoir.flyimpact.block.entity;

import com.altnoir.flyimpact.AltnoirBreeding;
import com.altnoir.flyimpact.breeding.SimpleBreedingChestBonus;
import com.altnoir.flyimpact.breeding.SimpleBreedingChestSlots;
import com.altnoir.flyimpact.menu.SimpleBreedingChestMenu;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import com.altnoir.poopsky.impl.PoTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;

public class SimpleBreedingChestBlockEntity extends BlockEntity implements MenuProvider {
    private int progress;
    private int currentInterval = SimpleBreedingChestSlots.BASE_INTERVAL;
    private int currentPoopBonus;
    private int currentMaggotsBonus;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case SimpleBreedingChestSlots.DATA_PROGRESS -> progress;
                case SimpleBreedingChestSlots.DATA_INTERVAL -> currentInterval;
                case SimpleBreedingChestSlots.DATA_POOP -> currentPoopBonus;
                case SimpleBreedingChestSlots.DATA_MAGGOTS -> currentMaggotsBonus;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case SimpleBreedingChestSlots.DATA_PROGRESS -> progress = value;
                case SimpleBreedingChestSlots.DATA_INTERVAL -> currentInterval = value;
                case SimpleBreedingChestSlots.DATA_POOP -> currentPoopBonus = value;
                case SimpleBreedingChestSlots.DATA_MAGGOTS -> currentMaggotsBonus = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return SimpleBreedingChestSlots.DATA_COUNT;
        }
    };

    private final ItemStackHandler itemHandler = new ItemStackHandler(SimpleBreedingChestSlots.TOTAL) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            refreshBonuses();
            syncToClient();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == SimpleBreedingChestSlots.FECES) {
                return stack.is(PoTags.Items.POOPS);
            }
            if (slot == SimpleBreedingChestSlots.FLY_1 || slot == SimpleBreedingChestSlots.FLY_2) {
                return FlyItem.isFlyItem(stack);
            }
            if (slot >= SimpleBreedingChestSlots.ACCEL_START
                    && slot < SimpleBreedingChestSlots.ACCEL_START + SimpleBreedingChestSlots.ACCEL_COUNT) {
                return SimpleBreedingChestBonus.isAccelerator(stack);
            }
            if (slot >= SimpleBreedingChestSlots.YIELD_START
                    && slot < SimpleBreedingChestSlots.YIELD_START + SimpleBreedingChestSlots.YIELD_COUNT) {
                return SimpleBreedingChestBonus.isParallelism(stack);
            }
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == SimpleBreedingChestSlots.FECES) {
                return SimpleBreedingChestSlots.FECES_LIMIT;
            }
            if (slot == SimpleBreedingChestSlots.FLY_1 || slot == SimpleBreedingChestSlots.FLY_2) {
                return 1;
            }
            return super.getSlotLimit(slot);
        }
    };

    private final IItemHandler topSideHandler = new RangedWrapper(itemHandler, SimpleBreedingChestSlots.FECES, SimpleBreedingChestSlots.OUTPUT_1) {
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };

    private final IItemHandler bottomHandler = new RangedWrapper(
            itemHandler,
            SimpleBreedingChestSlots.OUTPUT_1,
            SimpleBreedingChestSlots.OUTPUT_1 + SimpleBreedingChestSlots.OUTPUT_COUNT) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }
    };

    public SimpleBreedingChestBlockEntity(BlockPos pos, BlockState state) {
        super(FlyimpactBlockEntities.SIMPLE_BREEDING_CHEST.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SimpleBreedingChestBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        be.refreshBonuses();

        ItemStack fly1 = be.itemHandler.getStackInSlot(SimpleBreedingChestSlots.FLY_1);
        ItemStack fly2 = be.itemHandler.getStackInSlot(SimpleBreedingChestSlots.FLY_2);
        ItemStack feces = be.itemHandler.getStackInSlot(SimpleBreedingChestSlots.FECES);

        if (fly1.isEmpty() || fly2.isEmpty() || feces.isEmpty() || !FlyItem.isFlyItem(fly1) || !FlyItem.isFlyItem(fly2)) {
            be.progress = 0;
            be.currentInterval = SimpleBreedingChestSlots.BASE_INTERVAL;
            return;
        }

        if (be.areOutputsFull()) {
            return;
        }

        int neededFeces = 1 + be.currentMaggotsBonus;
        if (feces.getCount() < neededFeces) {
            be.progress = 0;
            be.currentInterval = SimpleBreedingChestSlots.BASE_INTERVAL;
            return;
        }

        be.currentInterval = Math.max(
                SimpleBreedingChestSlots.MIN_INTERVAL,
                SimpleBreedingChestSlots.BASE_INTERVAL - be.currentPoopBonus * SimpleBreedingChestSlots.TICKS_PER_ACCEL);
        be.progress++;

        if (be.progress >= be.currentInterval) {
            be.breed();
            be.progress = 0;
        }

        if (level.getRandom().nextDouble() < 0.005) {
            var workSound = BuiltInRegistries.SOUND_EVENT.get(
                    ResourceLocation.fromNamespaceAndPath("poopsky", "block.breeding_chest.work"));
            if (workSound != null) {
                level.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, workSound, SoundSource.BLOCKS, 1.0F, 1.2F);
            }
        }

        be.setChanged();
    }

    private void refreshBonuses() {
        int poop = 0;
        int maggots = 0;
        for (int i = 0; i < SimpleBreedingChestSlots.ACCEL_COUNT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(SimpleBreedingChestSlots.ACCEL_START + i);
            if (SimpleBreedingChestBonus.isAccelerator(stack)) {
                poop += stack.getCount();
            }
        }
        for (int i = 0; i < SimpleBreedingChestSlots.YIELD_COUNT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(SimpleBreedingChestSlots.YIELD_START + i);
            if (SimpleBreedingChestBonus.isParallelism(stack)) {
                maggots += stack.getCount();
            }
        }
        currentPoopBonus = poop;
        currentMaggotsBonus = maggots;
    }

    private boolean areOutputsFull() {
        for (int i = 0; i < SimpleBreedingChestSlots.OUTPUT_COUNT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(SimpleBreedingChestSlots.OUTPUT_1 + i);
            if (stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private void breed() {
        ItemStack fly1 = itemHandler.getStackInSlot(SimpleBreedingChestSlots.FLY_1);
        ItemStack fly2 = itemHandler.getStackInSlot(SimpleBreedingChestSlots.FLY_2);
        int count = 1 + currentMaggotsBonus;

        ItemStack feces = itemHandler.getStackInSlot(SimpleBreedingChestSlots.FECES);
        feces.shrink(count);
        if (feces.isEmpty()) {
            itemHandler.setStackInSlot(SimpleBreedingChestSlots.FECES, ItemStack.EMPTY);
        }

        for (int j = 0; j < count; j++) {
            PFlyRecipes.MutationResult result = PFlyRecipes.tryMutate(level, FlyItem.getFlyType(fly1), FlyItem.getFlyType(fly2));
            ItemStack flyProduct = FlyItem.withType(result.result());
            flyProduct.setCount(1);
            for (int i = 0; i < SimpleBreedingChestSlots.OUTPUT_COUNT; i++) {
                ItemStack remainder = tryInsert(SimpleBreedingChestSlots.OUTPUT_1 + i, flyProduct.copy());
                if (remainder.isEmpty()) {
                    break;
                }
                flyProduct = remainder;
            }
        }
        AltnoirBreeding.afterBreed(itemHandler, SimpleBreedingChestSlots.FLY_1, SimpleBreedingChestSlots.FLY_2);
    }

    private ItemStack tryInsert(int slot, ItemStack stack) {
        ItemStack current = itemHandler.getStackInSlot(slot);
        if (current.isEmpty()) {
            itemHandler.setStackInSlot(slot, stack.copy());
            return ItemStack.EMPTY;
        }
        if (ItemStack.isSameItemSameComponents(current, stack)) {
            int space = current.getMaxStackSize() - current.getCount();
            int toAdd = Math.min(space, stack.getCount());
            if (toAdd > 0) {
                current.grow(toAdd);
                itemHandler.setStackInSlot(slot, current);
                stack.shrink(toAdd);
            }
        }
        return stack;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IItemHandler getTopSideHandler() {
        return topSideHandler;
    }

    public IItemHandler getBottomHandler() {
        return bottomHandler;
    }

    public Container createContainerProxy() {
        return new SimpleContainer(SimpleBreedingChestSlots.TOTAL) {
            @Override
            public ItemStack getItem(int slot) {
                return itemHandler.getStackInSlot(slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if (stack.getCount() > itemHandler.getSlotLimit(slot)) {
                    stack = stack.copy();
                    stack.setCount(itemHandler.getSlotLimit(slot));
                }
                itemHandler.setStackInSlot(slot, stack);
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                return itemHandler.extractItem(slot, amount, false);
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                ItemStack stack = itemHandler.getStackInSlot(slot);
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
                return stack;
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return itemHandler.isItemValid(slot, stack);
            }

            @Override
            public void setChanged() {
                SimpleBreedingChestBlockEntity.this.setChanged();
            }

            @Override
            public boolean stillValid(Player player) {
                return Container.stillValidBlockEntity(SimpleBreedingChestBlockEntity.this, player);
            }

            @Override
            public int getContainerSize() {
                return SimpleBreedingChestSlots.TOTAL;
            }

            @Override
            public boolean isEmpty() {
                for (int i = 0; i < SimpleBreedingChestSlots.TOTAL; i++) {
                    if (!itemHandler.getStackInSlot(i).isEmpty()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.flyimpact.simple_breeding_chest");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new SimpleBreedingChestMenu(id, playerInventory, createContainerProxy(), data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        refreshBonuses();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
        }
    }
}

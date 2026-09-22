package com.altnoir.flyimpact.compat.sophisticatedbackpacks;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.barrel.BarrelTier;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class FlyBarrelUpgradeWrapper extends UpgradeWrapperBase<FlyBarrelUpgradeWrapper, FlyBarrelUpgradeItem>
        implements ITickableUpgrade {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOTS = BarrelTier.WOOD.outputSlots;
    public static final int SLOT_COUNT = 1 + OUTPUT_SLOTS;
    private static final int BASE_INTERVAL = 850;
    private static final int STACK_BONUS_PER_ITEM = 10;
    private static final int MIN_INTERVAL = 20;
    private static final int NOTHING_TO_DO_COOLDOWN = 10;

    private final ComponentItemHandler inventory;

    public FlyBarrelUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        this.inventory = new ComponentItemHandler(upgrade, BackpackCompat.INVENTORY.get(), SLOT_COUNT) {
            @Override
            protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {
                super.onContentsChanged(slot, oldStack, newStack);
                save();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (stack.isEmpty()) {
                    return true;
                }
                if (slot == INPUT_SLOT) {
                    return FlyItem.isFlyItem(stack);
                }
                ItemStack existing = getStackInSlot(slot);
                return existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return slot == INPUT_SLOT ? BarrelTier.INPUT_STACK_LIMIT : super.getSlotLimit(slot);
            }
        };
    }

    public ComponentItemHandler getInventory() {
        return inventory;
    }

    @Override
    public void tick(@Nullable Entity entity, Level level, BlockPos pos) {
        if (level.isClientSide() || isInCooldown(level)) {
            return;
        }
        if (!tickProduce(level)) {
            setCooldown(level, NOTHING_TO_DO_COOLDOWN);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            pause();
        }
        super.setEnabled(enabled);
    }

    @Override
    public void onBeforeRemoved() {
        pause();
    }

    public boolean isProducing() {
        return upgrade.getOrDefault(BackpackCompat.PRODUCING.get(), false);
    }

    public long getProduceFinish() {
        return upgrade.getOrDefault(BackpackCompat.PRODUCE_FINISH.get(), -1L);
    }

    public int getProduceTotal() {
        return upgrade.getOrDefault(BackpackCompat.PRODUCE_TOTAL.get(), 0);
    }

    private boolean tickProduce(Level level) {
        ItemStack input = inventory.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty() || !FlyItem.isFlyItem(input) || outputsFull()) {
            pause();
            return false;
        }
        int interval = intervalFor(input);
        if (isProducing()) {
            if (level.getGameTime() >= getProduceFinish()) {
                produce(level, input);
                if (!inventory.getStackInSlot(INPUT_SLOT).isEmpty() && !outputsFull()) {
                    start(level, interval);
                } else {
                    pause();
                }
            }
            return true;
        }
        start(level, interval);
        return true;
    }

    private void start(Level level, int interval) {
        upgrade.set(BackpackCompat.PRODUCING.get(), true);
        upgrade.set(BackpackCompat.PRODUCE_FINISH.get(), level.getGameTime() + interval);
        upgrade.set(BackpackCompat.PRODUCE_TOTAL.get(), interval);
        save();
    }

    private void pause() {
        if (!isProducing() && getProduceFinish() == -1L) {
            return;
        }
        upgrade.set(BackpackCompat.PRODUCING.get(), false);
        upgrade.set(BackpackCompat.PRODUCE_FINISH.get(), -1L);
        upgrade.set(BackpackCompat.PRODUCE_TOTAL.get(), 0);
        save();
    }

    private void produce(Level level, ItemStack input) {
        ItemStack product = PFlyRecipes.getProduct(level, FlyItem.getFlyType(input));
        if (product.isEmpty()) {
            return;
        }
        if (Flyimpact.FLY_TYPE_PUMPKIN.equals(FlyItem.getFlyType(input).id()) && level.getRandom().nextFloat() < 0.25F) {
            product = new ItemStack(Items.CARVED_PUMPKIN);
        } else {
            product = product.copy();
        }
        ItemStack remaining = product;
        for (int slot = 1; slot < SLOT_COUNT && !remaining.isEmpty(); slot++) {
            remaining = tryInsert(slot, remaining);
        }
    }

    private ItemStack tryInsert(int slot, ItemStack stack) {
        ItemStack current = inventory.getStackInSlot(slot);
        if (current.isEmpty()) {
            inventory.setStackInSlot(slot, stack.copy());
            return ItemStack.EMPTY;
        }
        if (ItemStack.isSameItemSameComponents(current, stack)) {
            int space = current.getMaxStackSize() - current.getCount();
            int toAdd = Math.min(space, stack.getCount());
            if (toAdd > 0) {
                current.grow(toAdd);
                inventory.setStackInSlot(slot, current);
                stack = stack.copy();
                stack.shrink(toAdd);
            }
        }
        return stack;
    }

    private boolean outputsFull() {
        for (int slot = 1; slot < SLOT_COUNT; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private static int intervalFor(ItemStack input) {
        return Math.max(MIN_INTERVAL, BASE_INTERVAL - (input.getCount() - 1) * STACK_BONUS_PER_ITEM);
    }
}

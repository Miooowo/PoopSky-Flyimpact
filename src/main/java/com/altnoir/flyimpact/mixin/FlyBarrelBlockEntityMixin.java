package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.barrel.BarrelTankInteraction;
import com.altnoir.flyimpact.barrel.BarrelTier;
import com.altnoir.flyimpact.barrel.FlyBarrelEnergyStorage;
import com.altnoir.flyimpact.barrel.FlyBarrelUpgradeAccess;
import com.altnoir.flyimpact.barrel.UpgradeAwareContainer;
import com.altnoir.flyimpact.item.UpgradeModuleItem;
import com.altnoir.flyimpact.menu.TieredFlyBarrelMenu;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyBarrelBlockEntity.class)
public abstract class FlyBarrelBlockEntityMixin implements FlyBarrelUpgradeAccess {
    @Shadow
    private int currentInterval;

    @Shadow
    @Final
    private ItemStackHandler itemHandler;

    @Shadow
    @Final
    @Mutable
    private IItemHandler topSideHandler;

    @Shadow
    @Final
    @Mutable
    private IItemHandler bottomHandler;

    @Shadow
    public abstract IItemHandler getItemHandler();

    @Unique
    private NonNullList<ItemStack> flyimpact$upgrades = NonNullList.withSize(1, ItemStack.EMPTY);

    @Unique
    private int flyimpact$transferCooldown;

    @Unique
    private int[] flyimpact$slotProgress = new int[1];

    @Unique
    private int[] flyimpact$slotInterval = new int[] {BarrelTier.BASE_TICK_INTERVAL};

    @Unique
    private FlyBarrelEnergyStorage flyimpact$energy;

    @Unique
    private FluidTank flyimpact$tank;

    @Unique
    private ContainerData flyimpact$slotData;

    @Unique
    private ContainerData flyimpact$storageSync;

    @Override
    public int flyimpact$inputSlots() {
        return BarrelTier.of(((FlyBarrelBlockEntity) (Object) this).getBlockState().getBlock()).inputSlots;
    }

    @Override
    public int flyimpact$inventorySlots() {
        return this.itemHandler.getSlots();
    }

    @Override
    public int flyimpact$upgradeSlots() {
        return this.flyimpact$upgrades.size();
    }

    @Override
    public ItemStack flyimpact$getUpgrade(int slot) {
        return slot >= 0 && slot < this.flyimpact$upgrades.size() ? this.flyimpact$upgrades.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public void flyimpact$setUpgrade(int slot, ItemStack stack) {
        if (slot < 0 || slot >= this.flyimpact$upgrades.size()) {
            return;
        }
        this.flyimpact$upgrades.set(slot, stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack);
        FlyBarrelBlockEntity be = (FlyBarrelBlockEntity) (Object) this;
        be.setChanged();
        be.invalidateCapabilities();
        ((FlyBarrelBlockEntityAccessor) be).flyimpact$syncToClient();
    }

    @Override
    public IEnergyStorage flyimpact$energy() {
        this.flyimpact$ensureBuffers();
        return UpgradeModuleItem.hasEnergy(this) ? this.flyimpact$energy : null;
    }

    @Override
    public IFluidHandler flyimpact$fluid() {
        this.flyimpact$ensureBuffers();
        return UpgradeModuleItem.hasFluid(this) ? this.flyimpact$tank : null;
    }

    @Override
    public ContainerData flyimpact$storageData() {
        this.flyimpact$ensureBuffers();
        return this.flyimpact$storageSync;
    }

    @Override
    public boolean flyimpact$interactTank(Player player) {
        if (!UpgradeModuleItem.hasFluid(this)) {
            return false;
        }
        this.flyimpact$ensureBuffers();
        return BarrelTankInteraction.interact(player, this.flyimpact$tank);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void flyimpact$initTier(BlockPos pos, BlockState state, CallbackInfo ci) {
        BarrelTier tier = BarrelTier.of(state.getBlock());
        this.flyimpact$upgrades = NonNullList.withSize(tier.upgradeSlots, ItemStack.EMPTY);
        this.flyimpact$ensureBuffers();
        this.flyimpact$ensureSlotProgress(tier.inputSlots);
        this.flyimpact$applyTier(tier);
    }

    @Inject(method = "createContainerProxy", at = @At("RETURN"), cancellable = true)
    private void flyimpact$wrapContainer(CallbackInfoReturnable<Container> cir) {
        cir.setReturnValue(new UpgradeAwareContainer(cir.getReturnValue(), this));
    }

    @Inject(method = "areOutputsFull", at = @At("HEAD"), cancellable = true, remap = false)
    private void flyimpact$outputsFull(CallbackInfoReturnable<Boolean> cir) {
        int inputs = this.flyimpact$inputSlots();
        for (int slot = inputs; slot < this.itemHandler.getSlots(); slot++) {
            ItemStack stack = this.itemHandler.getStackInSlot(slot);
            if (stack.getCount() < stack.getMaxStackSize()) {
                cir.setReturnValue(false);
                return;
            }
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "produce", at = @At("HEAD"), cancellable = true, remap = false)
    private void flyimpact$produceAll(CallbackInfo ci) {
        ci.cancel();
        Level level = ((FlyBarrelBlockEntity) (Object) this).getLevel();
        if (level == null) {
            return;
        }
        this.flyimpact$produceSlot(level, 0);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private static void flyimpact$tickInputs(Level level, BlockPos pos, BlockState state, FlyBarrelBlockEntity be, CallbackInfo ci) {
        if (level.isClientSide) {
            return;
        }
        FlyBarrelBlockEntityMixin mixin = (FlyBarrelBlockEntityMixin) (Object) be;
        int inputs = mixin.flyimpact$inputSlots();
        if (inputs <= 1) {
            return;
        }
        ci.cancel();
        mixin.flyimpact$tickIndependentInputs(level, pos);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/altnoir/poopsky/content/block/entity/FlyBarrelBlockEntity;currentInterval:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 1,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private static void flyimpact$applySpeed(Level level, BlockPos pos, BlockState state, FlyBarrelBlockEntity be, CallbackInfo ci) {
        int speed = UpgradeModuleItem.speedPercent((FlyBarrelUpgradeAccess) be);
        if (speed <= 0) {
            return;
        }
        FlyBarrelBlockEntityMixin mixin = (FlyBarrelBlockEntityMixin) (Object) be;
        mixin.currentInterval = Math.max(BarrelTier.MIN_INTERVAL, (int) (mixin.currentInterval * 100L / (100 + speed)));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void flyimpact$afterTick(Level level, BlockPos pos, BlockState state, FlyBarrelBlockEntity be, CallbackInfo ci) {
        if (level.isClientSide) {
            return;
        }
        FlyBarrelBlockEntityMixin mixin = (FlyBarrelBlockEntityMixin) (Object) be;
        if (mixin.flyimpact$inputSlots() > 1) {
            return;
        }
        mixin.flyimpact$exportItems(level, pos);
        mixin.flyimpact$generateEnergy(level, pos);
    }

    @Unique
    private void flyimpact$tickIndependentInputs(Level level, BlockPos pos) {
        int inputs = this.flyimpact$inputSlots();
        this.flyimpact$ensureSlotProgress(inputs);
        boolean outputsFull = this.flyimpact$outputsAreFull();
        int speed = UpgradeModuleItem.speedPercent(this);
        boolean working = false;
        for (int slot = 0; slot < inputs; slot++) {
            ItemStack stack = this.itemHandler.getStackInSlot(slot);
            if (stack.isEmpty() || !FlyItem.isFlyItem(stack)) {
                this.flyimpact$slotProgress[slot] = 0;
                this.flyimpact$slotInterval[slot] = BarrelTier.BASE_TICK_INTERVAL;
                continue;
            }
            if (outputsFull) {
                continue;
            }
            int interval = BarrelTier.intervalFor(stack.getCount(), speed);
            this.flyimpact$slotInterval[slot] = interval;
            this.flyimpact$slotProgress[slot]++;
            if (this.flyimpact$slotProgress[slot] >= interval) {
                this.flyimpact$produceSlot(level, slot);
                this.flyimpact$slotProgress[slot] = 0;
                outputsFull = this.flyimpact$outputsAreFull();
            }
            working = true;
        }
        this.flyimpact$exportItems(level, pos);
        this.flyimpact$generateEnergy(level, pos);
        if (working) {
            ((FlyBarrelBlockEntity) (Object) this).setChanged();
        }
    }

    @Unique
    private void flyimpact$produceSlot(Level level, int input) {
        ItemStack inputStack = this.itemHandler.getStackInSlot(input);
        if (inputStack.isEmpty() || !FlyItem.isFlyItem(inputStack)) {
            return;
        }
        ItemStack product = this.flyimpact$productFor(level, FlyItem.getFlyType(inputStack));
        if (product.isEmpty()) {
            return;
        }
        if (this.flyimpact$tryFillFluid(product)) {
            return;
        }
        ItemStack remaining = product.copy();
        int slots = this.itemHandler.getSlots();
        int inputs = this.flyimpact$inputSlots();
        for (int slot = inputs; slot < slots && !remaining.isEmpty(); slot++) {
            remaining = this.flyimpact$tryInsert(slot, remaining);
        }
    }

    @Unique
    private boolean flyimpact$tryFillFluid(ItemStack product) {
        if (!UpgradeModuleItem.hasFluid(this) || this.flyimpact$tank == null) {
            return false;
        }
        return FluidUtil.getFluidContained(product).map(contained -> {
            if (contained.isEmpty()) {
                return false;
            }
            FluidStack toFill = contained.copy();
            toFill.setAmount(contained.getAmount() * Math.max(1, product.getCount()));
            int filled = this.flyimpact$tank.fill(toFill, IFluidHandler.FluidAction.SIMULATE);
            if (filled < toFill.getAmount()) {
                return false;
            }
            this.flyimpact$tank.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }).orElse(false);
    }

    @Unique
    private void flyimpact$exportItems(Level level, BlockPos pos) {
        if (this.flyimpact$transferCooldown > 0) {
            this.flyimpact$transferCooldown--;
            return;
        }
        int multiplier = UpgradeModuleItem.transferMultiplier(this);
        if (multiplier <= 0) {
            return;
        }
        IItemHandler source = ((FlyBarrelBlockEntity) (Object) this).getItemHandler();
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
        if (target == null) {
            return;
        }
        int remaining = multiplier;
        int inputs = this.flyimpact$inputSlots();
        for (int slot = inputs; slot < source.getSlots() && remaining > 0; slot++) {
            while (remaining > 0) {
                ItemStack simulated = source.extractItem(slot, 1, true);
                if (simulated.isEmpty() || !ItemHandlerHelper.insertItemStacked(target, simulated, true).isEmpty()) {
                    break;
                }
                ItemStack extracted = source.extractItem(slot, 1, false);
                if (extracted.isEmpty()) {
                    break;
                }
                ItemHandlerHelper.insertItemStacked(target, extracted, false);
                remaining--;
            }
        }
        if (remaining < multiplier) {
            this.flyimpact$transferCooldown = BarrelTier.TRANSFER_INTERVAL;
            ((FlyBarrelBlockEntity) (Object) this).setChanged();
        }
    }

    @Unique
    private void flyimpact$generateEnergy(Level level, BlockPos pos) {
        this.flyimpact$ensureBuffers();
        if (!UpgradeModuleItem.hasEnergy(this)) {
            return;
        }
        int generate = 0;
        int inputs = this.flyimpact$inputSlots();
        for (int slot = 0; slot < inputs; slot++) {
            ItemStack stack = this.itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && FlyItem.isFlyItem(stack)) {
                generate += stack.getCount();
            }
        }
        if (generate > 0) {
            this.flyimpact$energy.generate(generate);
        }
        if (this.flyimpact$energy.getEnergyStored() <= 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IEnergyStorage target = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }
            int extracted = this.flyimpact$energy.extractEnergy(this.flyimpact$energy.getEnergyStored(), true);
            if (extracted <= 0) {
                return;
            }
            int accepted = target.receiveEnergy(extracted, false);
            if (accepted > 0) {
                this.flyimpact$energy.extractEnergy(accepted, false);
            }
        }
    }

    @Unique
    private boolean flyimpact$outputsAreFull() {
        int inputs = this.flyimpact$inputSlots();
        for (int slot = inputs; slot < this.itemHandler.getSlots(); slot++) {
            ItemStack stack = this.itemHandler.getStackInSlot(slot);
            if (stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private void flyimpact$ensureBuffers() {
        if (this.flyimpact$energy == null) {
            this.flyimpact$energy = new FlyBarrelEnergyStorage(BarrelTier.ENERGY_CAPACITY);
            this.flyimpact$energy.setOnChanged(() -> ((FlyBarrelBlockEntity) (Object) this).setChanged());
        }
        if (this.flyimpact$tank == null) {
            this.flyimpact$tank = new FluidTank(BarrelTier.FLUID_CAPACITY) {
                @Override
                protected void onContentsChanged() {
                    ((FlyBarrelBlockEntity) (Object) FlyBarrelBlockEntityMixin.this).setChanged();
                }
            };
        }
        if (this.flyimpact$storageSync == null) {
            this.flyimpact$storageSync = new ContainerData() {
                @Override
                public int get(int index) {
                    flyimpact$ensureBuffers();
                    return switch (index) {
                        case BarrelTier.STORAGE_ENERGY -> flyimpact$energy.getEnergyStored();
                        case BarrelTier.STORAGE_ENERGY_CAP -> flyimpact$energy.getMaxEnergyStored();
                        case BarrelTier.STORAGE_FLUID_AMOUNT -> flyimpact$tank.getFluidAmount();
                        case BarrelTier.STORAGE_FLUID_ID -> BuiltInRegistries.FLUID.getId(flyimpact$tank.getFluid().getFluid());
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
                }

                @Override
                public int getCount() {
                    return BarrelTier.STORAGE_DATA_SIZE;
                }
            };
        }
    }

    @Unique
    private void flyimpact$ensureSlotProgress(int inputs) {
        int size = Math.max(1, inputs);
        if (this.flyimpact$slotProgress.length != size) {
            this.flyimpact$slotProgress = new int[size];
            this.flyimpact$slotInterval = new int[size];
            java.util.Arrays.fill(this.flyimpact$slotInterval, BarrelTier.BASE_TICK_INTERVAL);
        }
        if (this.flyimpact$slotData == null) {
            this.flyimpact$slotData = new ContainerData() {
                @Override
                public int get(int index) {
                    int slot = index / 2;
                    if (slot < 0 || slot >= flyimpact$slotProgress.length) {
                        return 0;
                    }
                    return (index & 1) == 0 ? flyimpact$slotProgress[slot] : flyimpact$slotInterval[slot];
                }

                @Override
                public void set(int index, int value) {
                    int slot = index / 2;
                    if (slot < 0 || slot >= flyimpact$slotProgress.length) {
                        return;
                    }
                    if ((index & 1) == 0) {
                        flyimpact$slotProgress[slot] = value;
                    } else {
                        flyimpact$slotInterval[slot] = value;
                    }
                }

                @Override
                public int getCount() {
                    return Math.max(2, flyimpact$inputSlots() * 2);
                }
            };
        }
    }

    @Unique
    private ItemStack flyimpact$productFor(Level level, FlyType.Type type) {
        ItemStack product = PFlyRecipes.getProduct(level, type);
        if (product.isEmpty()) {
            return product;
        }
        if (Flyimpact.FLY_TYPE_PUMPKIN.equals(type.id()) && level.getRandom().nextFloat() < 0.25F) {
            product = new ItemStack(Items.CARVED_PUMPKIN);
        } else {
            product = product.copy();
        }
        int yield = UpgradeModuleItem.yieldPercent(this);
        if (yield > 0) {
            int percent = 100 + yield;
            int count = percent / 100;
            if (level.getRandom().nextInt(100) < percent % 100) {
                count++;
            }
            product.setCount(Math.min(product.getMaxStackSize(), Math.max(1, count)));
        }
        return product;
    }

    @Unique
    private ItemStack flyimpact$tryInsert(int slot, ItemStack stack) {
        ItemStack current = this.itemHandler.getStackInSlot(slot);
        if (current.isEmpty()) {
            this.itemHandler.setStackInSlot(slot, stack.copy());
            return ItemStack.EMPTY;
        }
        if (ItemStack.isSameItemSameComponents(current, stack)) {
            int space = current.getMaxStackSize() - current.getCount();
            int toAdd = Math.min(space, stack.getCount());
            if (toAdd > 0) {
                current.grow(toAdd);
                this.itemHandler.setStackInSlot(slot, current);
                stack.shrink(toAdd);
            }
        }
        return stack;
    }

    @Unique
    private void flyimpact$applyTier(BarrelTier tier) {
        int size = tier.inventorySlots;
        int oldSize = this.itemHandler.getSlots();
        if (oldSize != size) {
            ItemStack[] saved = new ItemStack[oldSize];
            for (int slot = 0; slot < oldSize; slot++) {
                saved[slot] = this.itemHandler.getStackInSlot(slot);
            }
            this.itemHandler.setSize(size);
            if (oldSize == 1 + tier.outputSlots && tier.inputSlots > 1) {
                this.itemHandler.setStackInSlot(0, saved[0]);
                int shift = tier.inputSlots - 1;
                for (int slot = 1; slot < oldSize && slot + shift < size; slot++) {
                    this.itemHandler.setStackInSlot(slot + shift, saved[slot]);
                }
            } else {
                int copy = Math.min(oldSize, size);
                for (int slot = 0; slot < copy; slot++) {
                    this.itemHandler.setStackInSlot(slot, saved[slot]);
                }
            }
        }
        this.topSideHandler = new RangedWrapper(this.itemHandler, 0, tier.inputSlots) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return super.isItemValid(slot, stack) && FlyItem.isFlyItem(stack);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }
        };
        this.bottomHandler = new RangedWrapper(this.itemHandler, tier.inputSlots, tier.inventorySlots) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    @Inject(method = "createMenu", at = @At("HEAD"), cancellable = true)
    private void flyimpact$tieredMenu(int containerId, Inventory playerInventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir) {
        BarrelTier tier = BarrelTier.of(((FlyBarrelBlockEntity) (Object) this).getBlockState().getBlock());
        if (tier == BarrelTier.WOOD) {
            return;
        }
        FlyBarrelBlockEntityAccessor accessor = (FlyBarrelBlockEntityAccessor) this;
        this.flyimpact$ensureSlotProgress(tier.inputSlots);
        cir.setReturnValue(new TieredFlyBarrelMenu(
                containerId,
                playerInventory,
                accessor.flyimpact$createContainerProxy(),
                this.flyimpact$slotData,
                tier));
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void flyimpact$title(CallbackInfoReturnable<Component> cir) {
        BarrelTier tier = BarrelTier.of(((FlyBarrelBlockEntity) (Object) this).getBlockState().getBlock());
        if (tier != BarrelTier.WOOD) {
            cir.setReturnValue(Component.translatable(tier.titleKey));
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void flyimpact$saveUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        ListTag upgrades = new ListTag();
        boolean any = false;
        for (ItemStack stack : this.flyimpact$upgrades) {
            if (!stack.isEmpty()) {
                upgrades.add((CompoundTag) stack.save(registries));
                any = true;
            } else {
                upgrades.add(new CompoundTag());
            }
        }
        if (any) {
            tag.put("flyimpact_upgrades", upgrades);
        }
        this.flyimpact$ensureSlotProgress(this.flyimpact$inputSlots());
        tag.putIntArray("flyimpact_slot_progress", this.flyimpact$slotProgress);
        tag.putIntArray("flyimpact_slot_interval", this.flyimpact$slotInterval);
        this.flyimpact$ensureBuffers();
        tag.putInt("flyimpact_energy", this.flyimpact$energy.getEnergyStored());
        tag.put("flyimpact_fluid", this.flyimpact$tank.writeToNBT(registries, new CompoundTag()));
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void flyimpact$loadUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.flyimpact$applyTier(BarrelTier.of(((FlyBarrelBlockEntity) (Object) this).getBlockState().getBlock()));
        this.flyimpact$ensureBuffers();
        this.flyimpact$ensureSlotProgress(this.flyimpact$inputSlots());
        for (int slot = 0; slot < this.flyimpact$upgrades.size(); slot++) {
            this.flyimpact$upgrades.set(slot, ItemStack.EMPTY);
        }
        if (tag.contains("flyimpact_upgrades", Tag.TAG_LIST)) {
            ListTag upgrades = tag.getList("flyimpact_upgrades", Tag.TAG_COMPOUND);
            int count = Math.min(upgrades.size(), this.flyimpact$upgrades.size());
            for (int slot = 0; slot < count; slot++) {
                this.flyimpact$upgrades.set(slot, ItemStack.parseOptional(registries, upgrades.getCompound(slot)));
            }
        } else if (tag.contains("flyimpact_upgrade")) {
            this.flyimpact$upgrades.set(0, ItemStack.parseOptional(registries, tag.getCompound("flyimpact_upgrade")));
        }
        if (tag.contains("flyimpact_slot_progress")) {
            int[] saved = tag.getIntArray("flyimpact_slot_progress");
            System.arraycopy(saved, 0, this.flyimpact$slotProgress, 0, Math.min(saved.length, this.flyimpact$slotProgress.length));
        }
        if (tag.contains("flyimpact_slot_interval")) {
            int[] saved = tag.getIntArray("flyimpact_slot_interval");
            System.arraycopy(saved, 0, this.flyimpact$slotInterval, 0, Math.min(saved.length, this.flyimpact$slotInterval.length));
        }
        this.flyimpact$energy.setEnergy(tag.getInt("flyimpact_energy"));
        if (tag.contains("flyimpact_fluid", Tag.TAG_COMPOUND)) {
            this.flyimpact$tank.readFromNBT(registries, tag.getCompound("flyimpact_fluid"));
        }
        ((FlyBarrelBlockEntity) (Object) this).invalidateCapabilities();
    }
}

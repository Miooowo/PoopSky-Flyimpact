package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.barrel.FlyBarrelContainerAccess;
import com.altnoir.flyimpact.barrel.UpgradeContainer;
import com.altnoir.flyimpact.item.UpgradeModuleItem;
import com.altnoir.poopsky.client.inventory.FlyBarrelMenu;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyBarrelMenu.class)
public abstract class FlyBarrelMenuMixin extends AbstractContainerMenu {
    @Unique
    private static final int FLYIMPACT_UPGRADE_X = 8;
    @Unique
    private static final int FLYIMPACT_UPGRADE_Y = 20;

    protected FlyBarrelMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V",
            at = @At("TAIL")
    )
    private void flyimpact$addUpgradeSlot(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data, CallbackInfo ci) {
        Container upgradeContainer = container instanceof FlyBarrelContainerAccess access
                ? new UpgradeContainer(access.flyimpact$upgradeAccess())
                : new SimpleContainer(1);
        this.addSlot(new Slot(upgradeContainer, 0, FLYIMPACT_UPGRADE_X, FLYIMPACT_UPGRADE_Y) {
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

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void flyimpact$quickMoveUpgrade(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        int upgradeIndex = this.slots.size() - 1;
        if (index < 0 || index >= this.slots.size()) {
            return;
        }
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return;
        }
        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();
        if (index == upgradeIndex) {
            if (!this.moveItemStackTo(source, 5, upgradeIndex, true)) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
            flyimpact$finishMove(slot, source, copy, player, cir);
            return;
        }
        if (index >= 5 && UpgradeModuleItem.isUpgrade(source)) {
            if (!this.moveItemStackTo(source, upgradeIndex, upgradeIndex + 1, false)) {
                return;
            }
            flyimpact$finishMove(slot, source, copy, player, cir);
        }
    }

    @Unique
    private void flyimpact$finishMove(Slot slot, ItemStack source, ItemStack copy, Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (source.getCount() == copy.getCount()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        slot.onTake(player, source);
        cir.setReturnValue(copy);
    }
}

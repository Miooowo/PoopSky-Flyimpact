package com.altnoir.flyimpact.compat.sophisticatedbackpacks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.common.gui.SlotSuppliedHandler;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;

public class FlyBarrelUpgradeContainer extends UpgradeContainerBase<FlyBarrelUpgradeWrapper, FlyBarrelUpgradeContainer> {
    public FlyBarrelUpgradeContainer(
            Player player,
            int upgradeContainerId,
            FlyBarrelUpgradeWrapper upgradeWrapper,
            UpgradeContainerType<FlyBarrelUpgradeWrapper, FlyBarrelUpgradeContainer> type) {
        super(player, upgradeContainerId, upgradeWrapper, type);
        slots.add(new SlotSuppliedHandler(supplyFromWrapper(FlyBarrelUpgradeWrapper::getInventory), FlyBarrelUpgradeWrapper.INPUT_SLOT, -30000, -30000));
        for (int slot = 1; slot < FlyBarrelUpgradeWrapper.SLOT_COUNT; slot++) {
            int outputSlot = slot;
            slots.add(new SlotSuppliedHandler(supplyFromWrapper(FlyBarrelUpgradeWrapper::getInventory), outputSlot, -30000, -30000) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }
    }

    @Override
    public void handlePacket(CompoundTag data) {
        // Slots are fully server-owned; the tab has no extra settings packets.
    }
}

package com.altnoir.flyimpact.barrel;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class BarrelTankInteraction {
    private BarrelTankInteraction() {
    }

    public static boolean interact(Player player, IFluidHandler tank) {
        if (player == null || tank == null) {
            return false;
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty() || carried.getCount() > 1) {
            return false;
        }
        FluidActionResult emptied = FluidUtil.tryEmptyContainer(carried, tank, Integer.MAX_VALUE, player, true);
        if (emptied.isSuccess()) {
            player.containerMenu.setCarried(emptied.getResult());
            return true;
        }
        FluidActionResult filled = FluidUtil.tryFillContainer(carried, tank, Integer.MAX_VALUE, player, true);
        if (filled.isSuccess()) {
            player.containerMenu.setCarried(filled.getResult());
            return true;
        }
        return false;
    }
}

package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.barrel.FlyBarrelUpgradeAccess;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import com.altnoir.poopsky.content.block.p.FlyBarrelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlyBarrelBlock.class)
public abstract class FlyBarrelBlockMixin {
    @ModifyConstant(method = "onRemove", constant = @Constant(intValue = 5))
    private int flyimpact$dropInventory(int original, BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof FlyBarrelBlockEntity be) {
            return be.getItemHandler().getSlots();
        }
        return original;
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void flyimpact$dropUpgrade(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (state.is(newState.getBlock()) || level.isClientSide) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof FlyBarrelBlockEntity be) {
            FlyBarrelUpgradeAccess access = (FlyBarrelUpgradeAccess) be;
            for (int slot = 0; slot < access.flyimpact$upgradeSlots(); slot++) {
                ItemStack upgrade = access.flyimpact$getUpgrade(slot);
                if (!upgrade.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), upgrade);
                    access.flyimpact$setUpgrade(slot, ItemStack.EMPTY);
                }
            }
        }
    }
}

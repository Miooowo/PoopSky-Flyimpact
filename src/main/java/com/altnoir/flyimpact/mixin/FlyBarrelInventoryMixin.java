package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.barrel.BarrelTier;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity$2", remap = false)
public abstract class FlyBarrelInventoryMixin {
    @Shadow
    @Final
    private FlyBarrelBlockEntity this$0;

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void flyimpact$inputValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        int inputs = BarrelTier.of(this.this$0.getBlockState().getBlock()).inputSlots;
        if (slot < inputs) {
            cir.setReturnValue(FlyItem.isFlyItem(stack));
        }
    }

    @Inject(method = "getSlotLimit", at = @At("HEAD"), cancellable = true)
    private void flyimpact$inputLimit(int slot, CallbackInfoReturnable<Integer> cir) {
        int inputs = BarrelTier.of(this.this$0.getBlockState().getBlock()).inputSlots;
        if (slot < inputs) {
            cir.setReturnValue(BarrelTier.INPUT_STACK_LIMIT);
        }
    }
}

package com.altnoir.flyimpact.mixin;

import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity$5")
public abstract class FlyBarrelContainerProxyMixin {
    @Shadow
    @Final
    private FlyBarrelBlockEntity this$0;

    @Inject(method = "getContainerSize", at = @At("HEAD"), cancellable = true)
    private void flyimpact$size(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.this$0.getItemHandler().getSlots());
    }

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    private void flyimpact$isEmpty(CallbackInfoReturnable<Boolean> cir) {
        var handler = this.this$0.getItemHandler();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (!handler.getStackInSlot(slot).isEmpty()) {
                cir.setReturnValue(false);
                return;
            }
        }
        cir.setReturnValue(true);
    }
}

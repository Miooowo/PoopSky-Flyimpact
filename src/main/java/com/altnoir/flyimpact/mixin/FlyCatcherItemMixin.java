package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.entity.FlyTypeHolder;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.item.p.FlyCatcherItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyCatcherItem.class)
public abstract class FlyCatcherItemMixin {
    @Inject(method = "getFlyTypeFromEntity", at = @At("HEAD"), cancellable = true)
    private static void flyimpact$readStoredType(FlyEntity fly, CallbackInfoReturnable<FlyType.Type> cir) {
        if (fly instanceof FlyTypeHolder holder) {
            cir.setReturnValue(new FlyType.Type(holder.flyimpact$getFlyType()));
        }
    }
}

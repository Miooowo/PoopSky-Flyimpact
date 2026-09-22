package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.AltnoirBreeding;
import com.altnoir.poopsky.content.block.entity.BreedingChestBlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreedingChestBlockEntity.class)
public abstract class BreedingChestBlockEntityMixin {
    @Shadow
    @Final
    private ItemStackHandler itemHandler;

    @Inject(method = "breed", at = @At("TAIL"))
    private void flyimpact$trackNormalFlyBreeds(CallbackInfo ci) {
        AltnoirBreeding.afterBreed(
                itemHandler,
                BreedingChestBlockEntity.SLOT_FLY_1,
                BreedingChestBlockEntity.SLOT_FLY_2);
    }
}

package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.block.TieredFlyBarrelBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void flyimpact$tieredBarrels(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!(state.getBlock() instanceof TieredFlyBarrelBlock)) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey((BlockEntityType<?>) (Object) this);
        if (id != null && "poopsky".equals(id.getNamespace()) && "fly_barrel".equals(id.getPath())) {
            cir.setReturnValue(true);
        }
    }
}

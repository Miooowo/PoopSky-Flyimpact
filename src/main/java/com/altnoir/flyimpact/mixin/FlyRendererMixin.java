package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.FlyAmbientEffects;
import com.altnoir.flyimpact.client.FlyEntityTextures;
import com.altnoir.flyimpact.client.FlyGlowLayer;
import com.altnoir.flyimpact.entity.FlyTypeHolder;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.entity.renderer.FlyRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyRenderer.class)
public abstract class FlyRendererMixin extends MobRenderer<FlyEntity, FlyModel<FlyEntity>> {
    public FlyRendererMixin(EntityRendererProvider.Context context, FlyModel<FlyEntity> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void flyimpact$addGlowLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.addLayer(new FlyGlowLayer(this));
    }

    @Override
    protected int getBlockLightLevel(FlyEntity entity, BlockPos pos) {
        return FlyAmbientEffects.isFullbright(entity) ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void flyimpact$typedTexture(FlyEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if (entity.isBaby() || !(entity instanceof FlyTypeHolder holder)) {
            return;
        }
        cir.setReturnValue(FlyEntityTextures.textureFor(holder.flyimpact$getFlyType()));
    }
}

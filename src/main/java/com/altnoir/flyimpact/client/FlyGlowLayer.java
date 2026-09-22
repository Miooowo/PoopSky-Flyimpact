package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.FlyAmbientEffects;
import com.altnoir.poopsky.content.entity.model.FlyModel;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class FlyGlowLayer extends RenderLayer<FlyEntity, FlyModel<FlyEntity>> {
    public FlyGlowLayer(RenderLayerParent<FlyEntity, FlyModel<FlyEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            FlyEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (!FlyAmbientEffects.isFullbright(entity)) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
        this.getParentModel().renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
    }
}

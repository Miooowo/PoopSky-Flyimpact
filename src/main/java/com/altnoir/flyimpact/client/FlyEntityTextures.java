package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.Flyimpact;
import net.minecraft.resources.ResourceLocation;

public final class FlyEntityTextures {
    private static final ResourceLocation DEFAULT_FLY =
            ResourceLocation.fromNamespaceAndPath("poopsky", "textures/entity/fly.png");
    private static final ResourceLocation BLUE_FLY =
            ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "textures/entity/flytest.png");

    private FlyEntityTextures() {
    }

    public static ResourceLocation textureFor(String flyType) {
        if (flyType == null || flyType.isEmpty() || "normal".equals(flyType)) {
            return DEFAULT_FLY;
        }
        if ("blue".equals(flyType)) {
            return BLUE_FLY;
        }
        return ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "textures/entity/fly_" + flyType + ".png");
    }
}

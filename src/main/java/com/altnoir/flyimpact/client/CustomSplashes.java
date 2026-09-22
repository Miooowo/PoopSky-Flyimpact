package com.altnoir.flyimpact.client;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.util.RandomSource;

import java.util.List;

public final class CustomSplashes {
    public static final List<String> LINES = List.of(
            "空中厕所，震撼美味！",
            "多彩蝇景现已发布！",
            "Shat by Miooo!",
            "Kind of shit free!",
            "Flys everywhere!",
            "Holy shit, man!",
            "It's a shit!",
            "Open source!",
            "欢迎加入蝇趴！"
    );

    private CustomSplashes() {
    }

    public static SplashRenderer randomRenderer() {
        return new SplashRenderer(LINES.get(RandomSource.create().nextInt(LINES.size())));
    }
}

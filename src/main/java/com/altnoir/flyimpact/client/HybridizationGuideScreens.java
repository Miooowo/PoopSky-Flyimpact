package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.compat.patchouli.PatchouliCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;

public final class HybridizationGuideScreens {
    private HybridizationGuideScreens() {
    }

    public static void open() {
        if (ModList.get().isLoaded("patchouli") && PatchouliCompat.openBook()) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.translatable("tooltip.flyimpact.hybridization_guide.requires_patchouli"), true);
        }
    }
}

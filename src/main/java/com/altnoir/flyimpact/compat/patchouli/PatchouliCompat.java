package com.altnoir.flyimpact.compat.patchouli;

import com.altnoir.flyimpact.Flyimpact;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.PatchouliAPI;

public final class PatchouliCompat {
    public static final ResourceLocation BOOK_ID = ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "hybridization_guide");

    private PatchouliCompat() {
    }

    public static boolean openBook() {
        if (PatchouliAPI.get().isStub()) {
            return false;
        }
        PatchouliAPI.get().openBookGUI(BOOK_ID);
        return true;
    }
}

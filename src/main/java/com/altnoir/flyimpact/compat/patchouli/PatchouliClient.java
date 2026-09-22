package com.altnoir.flyimpact.compat.patchouli;

import com.altnoir.flyimpact.Flyimpact;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.client.book.ClientBookRegistry;

public final class PatchouliClient {
    private PatchouliClient() {
    }

    public static void registerPageTypes() {
        ClientBookRegistry.INSTANCE.pageTypes.put(
                ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "relations"),
                OptionalRelationsPage.class
        );
    }
}

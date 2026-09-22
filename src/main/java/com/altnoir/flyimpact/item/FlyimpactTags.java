package com.altnoir.flyimpact.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class FlyimpactTags {
    public static final TagKey<Item> FLIES = common("flies");
    public static final TagKey<Item> POOPSKY_FLIES = poopsky("flies");

    private FlyimpactTags() {
    }

    private static TagKey<Item> common(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
    }

    private static TagKey<Item> poopsky(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("poopsky", name));
    }
}

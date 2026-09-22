package com.altnoir.flyimpact.client;

import com.altnoir.flyimpact.Flyimpact;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExtraFlyModels {
    private static final ResourceLocation FLY_ITEM_ID = ResourceLocation.fromNamespaceAndPath("poopsky", "fly");
    private static final ResourceLocation FLY_TYPE_COMPONENT_ID = ResourceLocation.fromNamespaceAndPath("poopsky", "fly_type");
    private static final Map<String, ResourceLocation> OVERRIDE_MODELS = Map.of(
            "blue", ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "item/flytest")
    );

    private ExtraFlyModels() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ExtraFlyModels::onRegisterAdditional);
        modEventBus.addListener(EventPriority.LOW, ExtraFlyModels::onModifyBakingResult);
    }

    private static ModelResourceLocation modelLocation(String flyType) {
        return new ModelResourceLocation(
                ResourceLocation.fromNamespaceAndPath("poopsky", "item/fly_" + flyType),
                ModelResourceLocation.STANDALONE_VARIANT);
    }

    private static ModelResourceLocation standalone(ResourceLocation model) {
        return new ModelResourceLocation(model, ModelResourceLocation.STANDALONE_VARIANT);
    }

    private static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (String flyType : Flyimpact.EXTRA_FLY_TYPES) {
            event.register(modelLocation(flyType));
        }
        for (ResourceLocation model : OVERRIDE_MODELS.values()) {
            event.register(standalone(model));
        }
    }

    @SuppressWarnings("unchecked")
    private static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        Map<String, BakedModel> extraModels = new LinkedHashMap<>();
        for (String flyType : Flyimpact.EXTRA_FLY_TYPES) {
            BakedModel extraModel = models.get(modelLocation(flyType));
            if (extraModel == null) {
                Flyimpact.LOGGER.warn("Missing extra fly item model: fly_{}", flyType);
                continue;
            }
            extraModels.put(flyType, extraModel);
        }
        for (Map.Entry<String, ResourceLocation> override : OVERRIDE_MODELS.entrySet()) {
            BakedModel overrideModel = models.get(standalone(override.getValue()));
            if (overrideModel == null) {
                Flyimpact.LOGGER.warn("Missing fly override model {} for type {}", override.getValue(), override.getKey());
                continue;
            }
            extraModels.put(override.getKey(), overrideModel);
        }
        if (extraModels.isEmpty()) {
            return;
        }

        DataComponentType<?> rawType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(FLY_TYPE_COMPONENT_ID);
        if (rawType == null) {
            Flyimpact.LOGGER.warn("Missing PoopSky fly_type data component");
            return;
        }
        DataComponentType<String> flyTypeComponent = (DataComponentType<String>) rawType;

        String flyId = FLY_ITEM_ID.toString();
        List<ModelResourceLocation> toWrap = new ArrayList<>();
        for (ModelResourceLocation key : models.keySet()) {
            String keyText = key.toString();
            if (keyText.startsWith(flyId + "#") || keyText.equals(flyId)) {
                toWrap.add(key);
            }
        }

        for (ModelResourceLocation key : toWrap) {
            BakedModel original = models.get(key);
            if (original != null) {
                models.put(key, new ExtraFlyBakedModel(original, extraModels, flyTypeComponent));
            }
        }
    }
}

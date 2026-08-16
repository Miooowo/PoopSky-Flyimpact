package com.altnoir.flyimpact.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ExtraFlyBakedModel extends BakedModelWrapper<BakedModel> {
    private final ItemOverrides overrides;

    public ExtraFlyBakedModel(BakedModel originalModel, Map<String, BakedModel> extraModels, DataComponentType<String> flyTypeComponent) {
        super(originalModel);
        this.overrides = new ExtraFlyOverrides(originalModel, extraModels, flyTypeComponent);
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    private static class ExtraFlyOverrides extends ItemOverrides {
        private final BakedModel originalModel;
        private final Map<String, BakedModel> extraModels;
        private final DataComponentType<String> flyTypeComponent;

        private ExtraFlyOverrides(BakedModel originalModel, Map<String, BakedModel> extraModels, DataComponentType<String> flyTypeComponent) {
            this.originalModel = originalModel;
            this.extraModels = extraModels;
            this.flyTypeComponent = flyTypeComponent;
        }

        @Override
        public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            String typeId = stack.get(flyTypeComponent);
            if (typeId != null) {
                BakedModel extraModel = extraModels.get(typeId);
                if (extraModel != null) {
                    return extraModel;
                }
            }
            ItemOverrides originalOverrides = originalModel.getOverrides();
            BakedModel resolved = originalOverrides.resolve(originalModel, stack, level, entity, seed);
            return resolved != null ? resolved : originalModel;
        }
    }
}

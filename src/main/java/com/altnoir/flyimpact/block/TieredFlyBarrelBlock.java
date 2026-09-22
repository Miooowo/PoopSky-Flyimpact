package com.altnoir.flyimpact.block;

import com.altnoir.flyimpact.barrel.BarrelTier;
import com.altnoir.poopsky.content.block.p.FlyBarrelBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public final class TieredFlyBarrelBlock extends FlyBarrelBlock {
    private final BarrelTier tier;

    public TieredFlyBarrelBlock(Properties properties, BarrelTier tier) {
        super(properties);
        this.tier = tier;
    }

    public BarrelTier tier() {
        return this.tier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(properties -> new TieredFlyBarrelBlock(properties, this.tier));
    }
}

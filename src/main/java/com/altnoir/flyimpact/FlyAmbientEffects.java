package com.altnoir.flyimpact;

import com.altnoir.flyimpact.entity.FlyTypeHolder;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;

public final class FlyAmbientEffects {
    private FlyAmbientEffects() {
    }

    public static boolean isFullbright(LivingEntity fly) {
        if (fly.isBaby() || !(fly instanceof FlyTypeHolder holder)) {
            return false;
        }
        String type = holder.flyimpact$getFlyType();
        return Flyimpact.FLY_TYPE_BLAZE.equals(type)
                || Flyimpact.FLY_TYPE_GLOWSTONE.equals(type)
                || Flyimpact.FLY_TYPE_NETHER_STAR.equals(type);
    }

    public static boolean isBlazeAdult(LivingEntity fly) {
        return !fly.isBaby()
                && fly instanceof FlyTypeHolder holder
                && Flyimpact.FLY_TYPE_BLAZE.equals(holder.flyimpact$getFlyType());
    }

    public static void spawnClientParticles(FlyEntity fly) {
        if (!fly.level().isClientSide || fly.isBaby() || !(fly instanceof FlyTypeHolder holder)) {
            return;
        }
        String type = holder.flyimpact$getFlyType();
        if (Flyimpact.FLY_TYPE_BLAZE.equals(type)) {
            fly.level().addParticle(
                    ParticleTypes.SMOKE,
                    fly.getRandomX(0.35),
                    fly.getRandomY(),
                    fly.getRandomZ(0.35),
                    0.0,
                    0.0,
                    0.0);
            if (fly.getRandom().nextBoolean()) {
                fly.level().addParticle(
                        ParticleTypes.FLAME,
                        fly.getRandomX(0.25),
                        fly.getY() + fly.getBbHeight() * 0.45,
                        fly.getRandomZ(0.25),
                        0.0,
                        0.0,
                        0.0);
            }
        }
    }
}

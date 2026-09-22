package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.FlyAmbientEffects;
import com.altnoir.flyimpact.entity.FlyTypeHolder;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.impl.type.damageType.PoDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyEntity.class)
public abstract class FlyEntityMixin extends Animal implements FlyTypeHolder {
    @Unique
    private static final EntityDataAccessor<String> FLYIMPACT$TYPE =
            SynchedEntityData.defineId(FlyEntity.class, EntityDataSerializers.STRING);
    @Unique
    private static final String FLYIMPACT$DEFAULT_TYPE = "normal";
    @Unique
    private static final String FLYIMPACT$NBT = "flyimpact_fly_type";

    protected FlyEntityMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void flyimpact$defineType(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(FLYIMPACT$TYPE, FLYIMPACT$DEFAULT_TYPE);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void flyimpact$readType(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains(FLYIMPACT$NBT)) {
            flyimpact$setFlyType(compound.getString(FLYIMPACT$NBT));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void flyimpact$writeType(CompoundTag compound, CallbackInfo ci) {
        compound.putString(FLYIMPACT$NBT, flyimpact$getFlyType());
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void flyimpact$markDeathType(DamageSource source, CallbackInfo ci) {
        if (source.is(DamageTypes.DROWN)) {
            flyimpact$setFlyType("blue");
        } else if (source.is(PoDamageTypes.ROUNDWORM)) {
            flyimpact$setFlyType("white");
        } else if (source.is(DamageTypes.CACTUS)) {
            flyimpact$setFlyType("green");
        } else if (source.is(PoDamageTypes.POOP_BALL)) {
            flyimpact$setFlyType("brown");
        }
    }

    @Inject(method = "thunderHit", at = @At("HEAD"))
    private void flyimpact$markLightningType(ServerLevel level, LightningBolt lightning, CallbackInfo ci) {
        flyimpact$setFlyType("black");
    }

    @Inject(method = "getBreedOffspring", at = @At("RETURN"))
    private void flyimpact$copyTypeToChild(ServerLevel level, AgeableMob otherParent, CallbackInfoReturnable<FlyEntity> cir) {
        FlyEntity child = cir.getReturnValue();
        if (!(child instanceof FlyTypeHolder holder)) {
            return;
        }
        String type = flyimpact$getFlyType();
        if (otherParent instanceof FlyTypeHolder other) {
            String otherType = other.flyimpact$getFlyType();
            if (!type.equals(otherType) && this.random.nextBoolean()) {
                type = otherType;
            }
        }
        holder.flyimpact$setFlyType(type);
    }

    @Override
    protected Component getTypeName() {
        if (this.isBaby()) {
            return super.getTypeName();
        }
        return Component.translatable("fly_type.poopsky." + flyimpact$getFlyType());
    }

    @Override
    public ItemStack getPickResult() {
        if (this.isBaby()) {
            return super.getPickResult();
        }
        return FlyItem.withType(flyimpact$getFlyType());
    }

    @Override
    public String flyimpact$getFlyType() {
        String type = this.getEntityData().get(FLYIMPACT$TYPE);
        return type == null || type.isEmpty() ? FLYIMPACT$DEFAULT_TYPE : type;
    }

    @Override
    public void flyimpact$setFlyType(String flyType) {
        String value = flyType == null || flyType.isEmpty() ? FLYIMPACT$DEFAULT_TYPE : flyType;
        this.getEntityData().set(FLYIMPACT$TYPE, value);
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return FlyAmbientEffects.isFullbright(this) ? 1.0F : super.getLightLevelDependentMagicValue();
    }

    @Override
    public boolean fireImmune() {
        return FlyAmbientEffects.isBlazeAdult(this) || super.fireImmune();
    }

    @Inject(method = "aiStep", at = @At("RETURN"))
    private void flyimpact$ambientParticles(CallbackInfo ci) {
        FlyAmbientEffects.spawnClientParticles((FlyEntity) (Object) this);
    }
}

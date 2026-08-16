package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.barrel.FlyBarrelUpgradeAccess;
import com.altnoir.flyimpact.barrel.UpgradeAwareContainer;
import com.altnoir.flyimpact.item.UpgradeModuleItem;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.block.entity.FlyBarrelBlockEntity;
import com.altnoir.poopsky.content.recipe.PFlyRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlyBarrelBlockEntity.class)
public abstract class FlyBarrelBlockEntityMixin implements FlyBarrelUpgradeAccess {
    @Shadow
    private int currentInterval;

    @Unique
    private ItemStack flyimpact$upgrade = ItemStack.EMPTY;

    @Override
    public ItemStack flyimpact$getUpgrade() {
        return flyimpact$upgrade;
    }

    @Override
    public void flyimpact$setUpgrade(ItemStack stack) {
        this.flyimpact$upgrade = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack;
        ((FlyBarrelBlockEntity) (Object) this).setChanged();
    }

    @Inject(method = "createContainerProxy", at = @At("RETURN"), cancellable = true)
    private void flyimpact$wrapContainer(CallbackInfoReturnable<Container> cir) {
        cir.setReturnValue(new UpgradeAwareContainer(cir.getReturnValue(), this));
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/altnoir/poopsky/content/block/entity/FlyBarrelBlockEntity;currentInterval:I",
                    opcode = Opcodes.PUTFIELD,
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private static void flyimpact$applySpeed(Level level, BlockPos pos, BlockState state, FlyBarrelBlockEntity be, CallbackInfo ci) {
        int speed = UpgradeModuleItem.speedPercent(((FlyBarrelUpgradeAccess) be).flyimpact$getUpgrade());
        if (speed <= 0) {
            return;
        }
        FlyBarrelBlockEntityMixin mixin = (FlyBarrelBlockEntityMixin) (Object) be;
        mixin.currentInterval = Math.max(20, (int) (mixin.currentInterval * 100L / (100 + speed)));
    }

    @Redirect(
            method = "produce",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/altnoir/poopsky/content/recipe/PFlyRecipes;getProduct(Lnet/minecraft/world/level/Level;Lcom/altnoir/poopsky/content/FlyType$Type;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack flyimpact$modifyProduct(Level level, FlyType.Type type) {
        ItemStack product = PFlyRecipes.getProduct(level, type);
        if (product.isEmpty() || level == null) {
            return product;
        }
        if (Flyimpact.FLY_TYPE_PUMPKIN.equals(type.id()) && level.getRandom().nextFloat() < 0.25F) {
            product = new ItemStack(Items.CARVED_PUMPKIN);
        } else {
            product = product.copy();
        }
        int yield = UpgradeModuleItem.yieldPercent(flyimpact$upgrade);
        if (yield > 0) {
            int percent = 100 + yield;
            int count = percent / 100;
            if (level.getRandom().nextInt(100) < percent % 100) {
                count++;
            }
            product.setCount(Math.min(product.getMaxStackSize(), Math.max(1, count)));
        }
        return product;
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void flyimpact$saveUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (!flyimpact$upgrade.isEmpty()) {
            tag.put("flyimpact_upgrade", flyimpact$upgrade.save(registries));
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void flyimpact$loadUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (tag.contains("flyimpact_upgrade")) {
            flyimpact$upgrade = ItemStack.parseOptional(registries, tag.getCompound("flyimpact_upgrade"));
        } else {
            flyimpact$upgrade = ItemStack.EMPTY;
        }
    }
}

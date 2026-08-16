package com.altnoir.flyimpact.mixin;

import com.altnoir.flyimpact.FlyLineage;
import com.altnoir.poopsky.content.FlyType;
import com.altnoir.poopsky.content.block.entity.BreedingChestBlockEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BreedingChestBlockEntity.class)
public abstract class BreedingChestBlockEntityMixin {
    @Shadow
    @Final
    private ItemStackHandler itemHandler;

    @Redirect(
            method = "breed",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/altnoir/poopsky/content/item/p/FlyItem;withType(Lcom/altnoir/poopsky/content/FlyType$Type;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack flyimpact$tagOffspring(FlyType.Type type) {
        ItemStack parent1 = this.itemHandler.getStackInSlot(BreedingChestBlockEntity.SLOT_FLY_1);
        ItemStack parent2 = this.itemHandler.getStackInSlot(BreedingChestBlockEntity.SLOT_FLY_2);
        if (!FlyItem.isFlyItem(parent1) || !FlyItem.isFlyItem(parent2)) {
            return FlyItem.withType(type);
        }
        return FlyLineage.createOffspring(parent1, parent2, type);
    }
}

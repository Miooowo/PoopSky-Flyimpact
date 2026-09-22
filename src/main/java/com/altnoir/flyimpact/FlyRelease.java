package com.altnoir.flyimpact;

import com.altnoir.flyimpact.entity.FlyTypeHolder;
import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class FlyRelease {
    private static final ResourceLocation FLY_ID = ResourceLocation.fromNamespaceAndPath("poopsky", "fly");

    private FlyRelease() {
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!FlyItem.isFlyItem(stack) || event.getFace() != Direction.UP || !event.getEntity().isShiftKeyDown()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }
        BlockPos spawnPos = event.getPos().relative(Direction.UP);
        FlyEntity fly = spawnTyped(
                level,
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                FlyItem.getFlyType(stack).id());
        if (fly == null) {
            return;
        }
        fly.playSound(SoundEvents.BEEHIVE_EXIT, 0.6F, 1.2F);
        stack.consume(1, event.getEntity());
    }

    static FlyEntity spawnTyped(Level level, double x, double y, double z, String flyType) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(FLY_ID);
        Entity created = entityType.create(level);
        if (!(created instanceof FlyEntity fly)) {
            return null;
        }
        fly.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
        if (fly instanceof FlyTypeHolder holder) {
            holder.flyimpact$setFlyType(flyType);
        }
        fly.setPersistenceRequired();
        level.addFreshEntity(fly);
        return fly;
    }
}

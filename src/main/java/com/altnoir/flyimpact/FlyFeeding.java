package com.altnoir.flyimpact;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public final class FlyFeeding {
    private record Feed(Item food, String flyType, SoundEvent sound) {
    }

    private static final List<Feed> FEEDS = List.of(
            new Feed(Items.BLAZE_ROD, Flyimpact.FLY_TYPE_BLAZE, SoundEvents.BLAZE_SHOOT),
            new Feed(Items.WHEAT, Flyimpact.FLY_TYPE_WHEAT, SoundEvents.CROP_BREAK),
            new Feed(Items.PUMPKIN, Flyimpact.FLY_TYPE_PUMPKIN, SoundEvents.WOOD_BREAK)
    );

    private FlyFeeding() {
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof FlyEntity fly) || !fly.isAlive()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        for (Feed feed : FEEDS) {
            if (!stack.is(feed.food())) {
                continue;
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            if (!event.getLevel().isClientSide) {
                fly.spawnAtLocation(FlyItem.withType(feed.flyType()));
                fly.playSound(feed.sound(), 0.6F, 1.2F);
                fly.kill();
                stack.consume(1, event.getEntity());
            }
            return;
        }
    }
}

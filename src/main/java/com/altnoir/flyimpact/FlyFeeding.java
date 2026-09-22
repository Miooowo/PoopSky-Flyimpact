package com.altnoir.flyimpact;

import com.altnoir.poopsky.content.entity.p.FlyEntity;
import com.altnoir.poopsky.content.item.p.FlyItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public final class FlyFeeding {
    private static final String ALTNOIR_NAME = "Altnoir";
    private static final String STEVE_NAME = "steve_";

    private record Feed(Item food, String flyType, SoundEvent sound) {
    }

    private record IdFeed(ResourceLocation food, String flyType, SoundEvent sound) {
    }

    private record NameConvert(String name, String flyType) {
    }

    private static final List<Feed> FEEDS = List.of(
            new Feed(Items.BLAZE_ROD, Flyimpact.FLY_TYPE_BLAZE, SoundEvents.BLAZE_SHOOT),
            new Feed(Items.WHEAT, Flyimpact.FLY_TYPE_WHEAT, SoundEvents.CROP_BREAK),
            new Feed(Items.PUMPKIN, Flyimpact.FLY_TYPE_PUMPKIN, SoundEvents.WOOD_BREAK),
            new Feed(Items.COAL_BLOCK, Flyimpact.FLY_TYPE_COAL, SoundEvents.STONE_BREAK),
            new Feed(Items.GRAVEL, Flyimpact.FLY_TYPE_GRAVEL, SoundEvents.GRAVEL_BREAK),
            new Feed(Items.CARROT, Flyimpact.FLY_TYPE_CARROT, SoundEvents.GENERIC_EAT),
            new Feed(Items.POTATO, Flyimpact.FLY_TYPE_POTATO, SoundEvents.GENERIC_EAT),
            new Feed(Items.BEETROOT, Flyimpact.FLY_TYPE_BEETROOT, SoundEvents.GENERIC_EAT),
            new Feed(Items.KELP, Flyimpact.FLY_TYPE_KELP, SoundEvents.GENERIC_EAT),
            new Feed(Items.COD, Flyimpact.FLY_TYPE_COD, SoundEvents.GENERIC_EAT),
            new Feed(Items.SALMON, Flyimpact.FLY_TYPE_SALMON, SoundEvents.GENERIC_EAT),
            new Feed(Items.NETHER_WART, Flyimpact.FLY_TYPE_NETHER_WART, SoundEvents.NETHER_WART_BREAK)
    );
    private static final List<IdFeed> ID_FEEDS = List.of(
            new IdFeed(id("poopsky", "dragon_breath_chili"), Flyimpact.FLY_TYPE_CHILI, SoundEvents.GENERIC_EAT),
            new IdFeed(id("poopsky", "saltpeter_shard"), Flyimpact.FLY_TYPE_SALTPETER, SoundEvents.AMETHYST_BLOCK_CHIME),
            new IdFeed(id("poopsky", "urea"), Flyimpact.FLY_TYPE_UREA, SoundEvents.BONE_MEAL_USE),
            new IdFeed(id("poopsky", "roundworm"), Flyimpact.FLY_TYPE_ROUNDWORM, SoundEvents.SLIME_SQUISH)
    );
    private static final List<NameConvert> NAME_CONVERTS = List.of(
            new NameConvert(ALTNOIR_NAME, Flyimpact.FLY_TYPE_ALTNOIR),
            new NameConvert(STEVE_NAME, Flyimpact.FLY_TYPE_STEVE)
    );

    private FlyFeeding() {
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof FlyEntity fly) || !fly.isAlive()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (tryNameTag(event, fly, stack)) {
            return;
        }
        ResourceLocation heldId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (IdFeed feed : ID_FEEDS) {
            if (!feed.food().equals(heldId)) {
                continue;
            }
            convertFly(event, fly, stack, feed.flyType(), feed.sound());
            return;
        }
        for (Feed feed : FEEDS) {
            if (!stack.is(feed.food())) {
                continue;
            }
            convertFly(event, fly, stack, feed.flyType(), feed.sound());
            return;
        }
    }

    private static boolean tryNameTag(PlayerInteractEvent.EntityInteract event, FlyEntity fly, ItemStack stack) {
        if (!stack.is(Items.NAME_TAG)) {
            return false;
        }
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name == null) {
            return false;
        }
        String value = name.getString().strip();
        for (NameConvert convert : NAME_CONVERTS) {
            if (!convert.name().equalsIgnoreCase(value)) {
                continue;
            }
            convertFly(event, fly, stack, convert.flyType(), SoundEvents.PLAYER_LEVELUP);
            return true;
        }
        return false;
    }

    private static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private static void convertFly(
            PlayerInteractEvent.EntityInteract event,
            FlyEntity fly,
            ItemStack stack,
            String flyType,
            SoundEvent sound) {
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!event.getLevel().isClientSide) {
            fly.spawnAtLocation(FlyItem.withType(flyType));
            fly.playSound(sound, 0.6F, 1.2F);
            fly.kill();
            stack.consume(1, event.getEntity());
        }
    }
}

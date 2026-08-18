package com.altnoir.flyimpact.event;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.item.FlyimpactItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class GiveHybridizationGuide {
    private static final String RECEIVED_TAG = Flyimpact.MOD_ID + ":received_guide";
    private static final ResourceLocation ADVANCEMENT_ID =
            ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "grant_book_on_first_join");

    private GiveHybridizationGuide() {
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!ModList.get().isLoaded("patchouli")) {
            return;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        server.execute(() -> tryGive(player));
    }

    private static void tryGive(ServerPlayer player) {
        if (!player.isAlive() || player.hasDisconnected()) {
            return;
        }
        if (awardAdvancement(player)) {
            return;
        }
        giveWithPersistentTag(player);
    }

    private static boolean awardAdvancement(ServerPlayer player) {
        AdvancementHolder holder = player.server.getAdvancements().get(ADVANCEMENT_ID);
        if (holder == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (progress.isDone()) {
            return true;
        }
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criterion);
        }
        return true;
    }

    private static void giveWithPersistentTag(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        CompoundTag persistent = data.getCompound(Player.PERSISTED_NBT_TAG);
        if (persistent.getBoolean(RECEIVED_TAG)) {
            return;
        }
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(FlyimpactItems.HYBRIDIZATION_GUIDE.get()));
        persistent.putBoolean(RECEIVED_TAG, true);
        data.put(Player.PERSISTED_NBT_TAG, persistent);
    }
}

package com.altnoir.flyimpact.network;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.flyimpact.menu.FlyBarrelStorageMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BarrelTankClickPayload(int containerId) implements CustomPacketPayload {
    public static final Type<BarrelTankClickPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "barrel_tank_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BarrelTankClickPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, BarrelTankClickPayload::containerId, BarrelTankClickPayload::new);

    @Override
    public Type<BarrelTankClickPayload> type() {
        return TYPE;
    }

    public static void handle(BarrelTankClickPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AbstractContainerMenu menu = player.containerMenu;
            if (menu.containerId != payload.containerId()) {
                return;
            }
            if (menu instanceof FlyBarrelStorageMenu storage) {
                storage.flyimpact$interactTank(player);
            }
        });
    }
}

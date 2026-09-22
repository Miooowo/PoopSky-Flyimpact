package com.altnoir.flyimpact.network;

import com.altnoir.flyimpact.Flyimpact;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class FlyimpactNetwork {
    private FlyimpactNetwork() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(FlyimpactNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Flyimpact.MOD_ID).versioned("1");
        registrar.playToServer(
                BarrelTankClickPayload.TYPE,
                BarrelTankClickPayload.STREAM_CODEC,
                BarrelTankClickPayload::handle);
    }
}

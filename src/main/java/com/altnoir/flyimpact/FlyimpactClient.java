package com.altnoir.flyimpact;

import com.altnoir.flyimpact.client.ExtraFlyModels;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Flyimpact.MOD_ID, dist = Dist.CLIENT)
public class FlyimpactClient {
    public FlyimpactClient(IEventBus modEventBus, ModContainer modContainer) {
        ExtraFlyModels.register(modEventBus);
    }
}

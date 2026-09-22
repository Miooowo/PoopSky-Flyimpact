package com.altnoir.flyimpact;

import com.altnoir.flyimpact.client.ExtraFlyModels;
import com.altnoir.flyimpact.client.SimpleBreedingChestScreen;
import com.altnoir.flyimpact.client.TieredFlyBarrelScreen;
import com.altnoir.flyimpact.compat.patchouli.PatchouliClient;
import com.altnoir.flyimpact.compat.sophisticatedbackpacks.client.BackpackCompatClient;
import com.altnoir.flyimpact.menu.FlyimpactMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = Flyimpact.MOD_ID, dist = Dist.CLIENT)
public class FlyimpactClient {
    public FlyimpactClient(IEventBus modEventBus, ModContainer modContainer) {
        ExtraFlyModels.register(modEventBus);
        modEventBus.addListener(FlyimpactClient::onRegisterScreens);
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliClient.registerPageTypes();
        }
    }

    private static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(FlyimpactMenus.COPPER_FLY_BARREL.get(), TieredFlyBarrelScreen::new);
        event.register(FlyimpactMenus.IRON_FLY_BARREL.get(), TieredFlyBarrelScreen::new);
        event.register(FlyimpactMenus.SIMPLE_BREEDING_CHEST.get(), SimpleBreedingChestScreen::new);
        if (ModList.get().isLoaded("sophisticatedbackpacks")) {
            BackpackCompatClient.register();
        }
    }
}

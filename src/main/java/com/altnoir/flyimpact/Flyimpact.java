package com.altnoir.flyimpact;

import com.altnoir.flyimpact.item.FlyimpactItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(Flyimpact.MOD_ID)
public class Flyimpact {
    public static final String MOD_ID = "flyimpact";
    public static final String FLY_TYPE_LEATHER = "leather";
    public static final String FLY_TYPE_ROTTEN_FLESH = "rotten_flesh";
    public static final String FLY_TYPE_BLAZE = "blaze";
    public static final String FLY_TYPE_WHEAT = "wheat";
    public static final String FLY_TYPE_PUMPKIN = "pumpkin";
    public static final String FLY_TYPE_FAIRY = "fairy";
    public static final List<String> EXTRA_FLY_TYPES = List.of(
            FLY_TYPE_LEATHER,
            FLY_TYPE_ROTTEN_FLESH,
            FLY_TYPE_BLAZE,
            FLY_TYPE_WHEAT,
            FLY_TYPE_PUMPKIN,
            FLY_TYPE_FAIRY
    );
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public Flyimpact(IEventBus modEventBus, ModContainer modContainer) {
        FlyimpactItems.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(FlyTypeInjector::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(FlyTypeInjector::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(FlyFeeding::onEntityInteract);
        LOGGER.info("PoopSky: Flyimpact loaded");
    }
}

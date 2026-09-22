package com.altnoir.flyimpact.compat.sophisticatedbackpacks.client;

import com.altnoir.flyimpact.compat.sophisticatedbackpacks.BackpackCompat;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;

public final class BackpackCompatClient {
    private BackpackCompatClient() {
    }

    public static void register() {
        UpgradeGuiManager.registerTab(BackpackCompat.CONTAINER_TYPE, FlyBarrelUpgradeTab::new);
    }
}

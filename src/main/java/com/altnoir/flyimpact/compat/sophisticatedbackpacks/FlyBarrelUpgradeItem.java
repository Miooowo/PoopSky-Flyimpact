package com.altnoir.flyimpact.compat.sophisticatedbackpacks;

import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;

import java.util.List;

public class FlyBarrelUpgradeItem extends UpgradeItemBase<FlyBarrelUpgradeWrapper> {
    private static final UpgradeType<FlyBarrelUpgradeWrapper> TYPE = new UpgradeType<>(FlyBarrelUpgradeWrapper::new);

    public FlyBarrelUpgradeItem() {
        super(BackpackCompat.ONE_PER_STORAGE);
    }

    @Override
    public UpgradeType<FlyBarrelUpgradeWrapper> getType() {
        return TYPE;
    }

    @Override
    public List<IUpgradeItem.UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }
}

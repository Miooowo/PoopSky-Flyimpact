package com.altnoir.flyimpact.barrel;

import net.neoforged.neoforge.energy.EnergyStorage;

public final class FlyBarrelEnergyStorage extends EnergyStorage {
    private Runnable onChanged = () -> {
    };

    public FlyBarrelEnergyStorage(int capacity) {
        super(capacity, capacity, capacity);
    }

    public void setOnChanged(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public int generate(int amount) {
        int accepted = Math.min(this.capacity - this.energy, Math.max(0, amount));
        if (accepted > 0) {
            this.energy += accepted;
            this.onChanged.run();
        }
        return accepted;
    }

    public void setEnergy(int value) {
        this.energy = Math.max(0, Math.min(this.capacity, value));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) {
            this.onChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) {
            this.onChanged.run();
        }
        return extracted;
    }
}

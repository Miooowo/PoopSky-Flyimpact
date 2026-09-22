package com.altnoir.flyimpact.barrel;

import com.altnoir.flyimpact.block.FlyimpactBlocks;
import net.minecraft.world.level.block.Block;

public enum BarrelTier {
    WOOD(1, 4, 1, "container.poopsky.fly_barrel"),
    COPPER(2, 8, 2, "container.flyimpact.copper_fly_barrel"),
    IRON(3, 12, 3, "container.flyimpact.iron_fly_barrel");

    public static final int BASE_INPUT_SLOTS = 1;
    public static final int BASE_OUTPUT_SLOTS = 4;
    public static final int OUTPUTS_PER_ROW = 4;
    public static final int BASE_GUI_HEIGHT = 132;
    public static final int SLOT_SIZE = 18;
    public static final int INPUT_X = 26;
    public static final int INPUT_Y = 20;
    public static final int OUTPUT_X = 80;
    public static final int OUTPUT_Y = 20;
    public static final int HEADER_HEIGHT = OUTPUT_Y + SLOT_SIZE;
    public static final int PLAYER_PANEL_HEIGHT = BASE_GUI_HEIGHT - HEADER_HEIGHT;
    public static final int ARROW_X = 48;
    public static final int ARROW_Y = 19;
    public static final int ARROW_WIDTH = 26;
    public static final int ARROW_HEIGHT = 16;
    public static final int PLAYER_Y = 51;
    public static final int HOTBAR_Y = 109;
    public static final int TRANSFER_INTERVAL = 8;
    public static final int INPUT_STACK_LIMIT = 88;
    public static final int BASE_TICK_INTERVAL = 850;
    public static final int STACK_BONUS_PER_ITEM = 10;
    public static final int MIN_INTERVAL = 20;
    public static final int FLUID_CAPACITY = 32000;
    public static final int ENERGY_CAPACITY = 100000;
    public static final int STORAGE_DATA_SIZE = 4;
    public static final int STORAGE_ENERGY = 0;
    public static final int STORAGE_ENERGY_CAP = 1;
    public static final int STORAGE_FLUID_AMOUNT = 2;
    public static final int STORAGE_FLUID_ID = 3;

    public static int intervalFor(int flyCount, int speedPercent) {
        int stackBonus = Math.max(0, flyCount - 1) * STACK_BONUS_PER_ITEM;
        int interval = Math.max(MIN_INTERVAL, BASE_TICK_INTERVAL - stackBonus);
        if (speedPercent > 0) {
            interval = Math.max(MIN_INTERVAL, (int) (interval * 100L / (100 + speedPercent)));
        }
        return interval;
    }

    public final int inputSlots;
    public final int outputSlots;
    public final int upgradeSlots;
    public final int inventorySlots;
    public final String titleKey;

    BarrelTier(int inputSlots, int outputSlots, int upgradeSlots, String titleKey) {
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.upgradeSlots = upgradeSlots;
        this.inventorySlots = inputSlots + outputSlots;
        this.titleKey = titleKey;
    }

    public int extraRows() {
        return Math.max(this.inputSlots - BASE_INPUT_SLOTS, (this.outputSlots - BASE_OUTPUT_SLOTS) / OUTPUTS_PER_ROW);
    }

    public int guiOffset() {
        return this.extraRows() * SLOT_SIZE;
    }

    public int imageHeight() {
        return BASE_GUI_HEIGHT + this.guiOffset();
    }

    public static BarrelTier of(Block block) {
        if (block == FlyimpactBlocks.IRON_FLY_BARREL.get()) {
            return IRON;
        }
        if (block == FlyimpactBlocks.COPPER_FLY_BARREL.get()) {
            return COPPER;
        }
        return WOOD;
    }
}

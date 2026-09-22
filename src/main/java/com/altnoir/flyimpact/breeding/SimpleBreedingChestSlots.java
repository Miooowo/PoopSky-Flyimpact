package com.altnoir.flyimpact.breeding;

public final class SimpleBreedingChestSlots {
    public static final int FECES = 0;
    public static final int FLY_1 = 1;
    public static final int FLY_2 = 2;
    public static final int OUTPUT_1 = 3;
    public static final int OUTPUT_COUNT = 3;
    public static final int ACCEL_START = 6;
    public static final int ACCEL_COUNT = 4;
    public static final int YIELD_START = 10;
    public static final int YIELD_COUNT = 4;
    public static final int TOTAL = 14;

    public static final int FECES_LIMIT = 88;
    public static final int BASE_INTERVAL = 1200;
    public static final int MIN_INTERVAL = 20;
    public static final int TICKS_PER_ACCEL = 10;

    public static final int DATA_COUNT = 4;
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_INTERVAL = 1;
    public static final int DATA_POOP = 2;
    public static final int DATA_MAGGOTS = 3;

    /** Original breeding chest: inventory label starts at this Y. */
    public static final int ORIGINAL_PLAYER_PANEL_V = 38;
    public static final int ORIGINAL_PLAYER_INV_Y = 51;
    public static final int ORIGINAL_HOTBAR_Y = 109;
    public static final int ORIGINAL_PLAYER_PANEL_HEIGHT = 94;
    /**
     * Inner panel gray taken from the title area (below the window bezel, above the machine slots).
     * Do not copy the inventory header — that strip contains a dark divider line.
     */
    public static final int FILL_V = 4;
    public static final int FILL_HEIGHT = 12;

    public static final int SLOT_SIZE = 18;
    public static final int EXTRA_GAP = 8;
    public static final int MACHINE_HEIGHT = 38;
    public static final int EXTRA_SLOT_Y = MACHINE_HEIGHT + EXTRA_GAP;
    public static final int PLAYER_PANEL_Y = EXTRA_SLOT_Y + SLOT_SIZE + EXTRA_GAP;
    public static final int EXTRA_SHIFT = PLAYER_PANEL_Y - ORIGINAL_PLAYER_PANEL_V;
    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 132 + EXTRA_SHIFT;
    public static final int PLAYER_PANEL_V = ORIGINAL_PLAYER_PANEL_V;
    public static final int PLAYER_PANEL_HEIGHT = ORIGINAL_PLAYER_PANEL_HEIGHT;
    public static final int PLAYER_INV_Y = PLAYER_PANEL_Y + (ORIGINAL_PLAYER_INV_Y - ORIGINAL_PLAYER_PANEL_V);
    public static final int HOTBAR_Y = PLAYER_PANEL_Y + (ORIGINAL_HOTBAR_Y - ORIGINAL_PLAYER_PANEL_V);
    public static final int INVENTORY_LABEL_Y = PLAYER_PANEL_Y;

    public static final int FECES_X = 8;
    public static final int FECES_Y = 20;
    public static final int FLY_1_X = 44;
    public static final int FLY_2_X = 62;
    public static final int FLY_Y = 20;
    public static final int OUTPUT_X = 116;
    public static final int OUTPUT_Y = 20;

    public static final int PROGRESS_X = 84;
    public static final int PROGRESS_Y = 22;
    public static final int PROGRESS_U = 176;
    public static final int PROGRESS_WIDTH = 26;
    public static final int PROGRESS_HEIGHT = 12;
    public static final int SLOT_SPRITE_U = 7;
    public static final int SLOT_SPRITE_V = 19;
    public static final int SLOT_SPRITE = 18;

    private SimpleBreedingChestSlots() {
    }

    public static int accelX(int index) {
        return 8 + index * 18;
    }

    public static int yieldX(int index) {
        return 98 + index * 18;
    }
}

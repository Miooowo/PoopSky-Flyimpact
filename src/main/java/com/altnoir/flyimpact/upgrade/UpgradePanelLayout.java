package com.altnoir.flyimpact.upgrade;

/**
 * 机器 GUI 左侧升级侧栏的槽位坐标。服务端菜单与客户端绘制共用。
 */
public final class UpgradePanelLayout {
    public static final int SLOT_X = -21;
    public static final int SLOT_Y = 8;
    public static final int SLOT_SPACING = 18;
    public static final int PANEL_WIDTH = 32;
    public static final int PANEL_OFFSET_X = -28;
    public static final int PANEL_OFFSET_Y = 4;
    public static final int TOP_CAP = 4;
    public static final int BOTTOM_CAP = 4;

    private UpgradePanelLayout() {
    }

    public static int slotY(int index) {
        return SLOT_Y + index * SLOT_SPACING;
    }

    public static int panelHeight(int slotCount) {
        return TOP_CAP + slotCount * SLOT_SPACING + BOTTOM_CAP;
    }
}

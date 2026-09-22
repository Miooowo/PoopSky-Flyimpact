package com.altnoir.flyimpact.upgrade;

/**
 * 苍蝇桶左侧升级槽。坐标与精妙背包升级栏一致：贴在界面左上角，顶边与主界面共用，右边框并入主界面左边框。
 * 服务端菜单与客户端绘制共用。
 */
public final class UpgradePanelLayout {
    /** 物品区左上角。凹槽本身画在贴图里，不再额外内缩。 */
    public static final int SLOT_X = -15;
    public static final int SLOT_Y = 6;
    public static final int SLOT_SPACING = 16;
    /** 侧栏贴图左上角，相对界面原点。贴图左侧有 2 像素透明边。 */
    public static final int PANEL_OFFSET_X = -21;
    public static final int PANEL_OFFSET_Y = 0;
    public static final int PANEL_WIDTH = 26;
    public static final int PANEL_BODY_WIDTH = 25;
    public static final int PANEL_TOP = 4;
    public static final int PANEL_BOTTOM = 6;
    /** 底盖在贴图中的 v。 */
    public static final int PANEL_BOTTOM_V = 198;

    private UpgradePanelLayout() {
    }

    public static int slotY(int index) {
        return SLOT_Y + index * SLOT_SPACING;
    }

    /** 底盖之上的高度：固定 6 像素收口加上每个槽 16 像素。 */
    public static int heightWithoutBottom(int slotCount) {
        return PANEL_BOTTOM + slotCount * SLOT_SPACING;
    }

    public static int panelHeight(int slotCount) {
        return heightWithoutBottom(slotCount) + PANEL_BOTTOM;
    }
}

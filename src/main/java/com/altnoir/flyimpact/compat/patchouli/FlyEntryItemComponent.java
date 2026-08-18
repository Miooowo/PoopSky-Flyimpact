package com.altnoir.flyimpact.compat.patchouli;

import com.altnoir.flyimpact.Flyimpact;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.function.UnaryOperator;

/**
 * Renders a fly stack and opens that variety's Patchouli entry on click.
 * Patchouli's {@code link_recipe} lookup keys stacks by item id only, so every
 * {@code poopsky:fly} would otherwise jump to the same chapter.
 */
public final class FlyEntryItemComponent implements ICustomComponent {
    public String item = "";
    public boolean framed;

    private transient int x;
    private transient int y;
    private transient ItemStack stack = ItemStack.EMPTY;
    private transient ResourceLocation entryId;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        IVariable resolved = lookup.apply(IVariable.wrap(this.item, registries));
        this.stack = parseStack(resolved);
        this.entryId = entryIdFor(this.stack);
    }

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (this.framed) {
            RenderSystem.enableBlend();
            graphics.setColor(1F, 1F, 1F, 1F);
            graphics.blit(context.getCraftingTexture(), this.x - 5, this.y - 5, 20, 102, 26, 26, 128, 256);
        }
        if (!this.stack.isEmpty()) {
            context.renderItemStack(graphics, this.x, this.y, mouseX, mouseY, this.stack);
        }
    }

    @Override
    public boolean mouseClicked(IComponentRenderContext context, double mouseX, double mouseY, int mouseButton) {
        if (mouseButton != 0 || this.entryId == null || this.stack.isEmpty()) {
            return false;
        }
        if (!context.isAreaHovered((int) mouseX, (int) mouseY, this.x, this.y, 16, 16)) {
            return false;
        }
        return context.navigateToEntry(this.entryId, 0, true);
    }

    private static ItemStack parseStack(IVariable variable) {
        if (variable == null || variable.unwrap().isJsonNull()) {
            return ItemStack.EMPTY;
        }
        try {
            ItemStack[] stacks = variable.as(ItemStack[].class);
            if (stacks != null && stacks.length > 0 && stacks[0] != null && !stacks[0].isEmpty()) {
                return stacks[0];
            }
        } catch (RuntimeException ignored) {
            // Fall through and try a single stack.
        }
        try {
            ItemStack parsed = variable.as(ItemStack.class);
            return parsed == null ? ItemStack.EMPTY : parsed;
        } catch (RuntimeException ignored) {
            return ItemStack.EMPTY;
        }
    }

    private static ResourceLocation entryIdFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        String typeId = FlyItem.getFlyType(stack).id();
        if (typeId == null || typeId.isBlank()) {
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath(Flyimpact.MOD_ID, "flies/" + typeId);
    }
}

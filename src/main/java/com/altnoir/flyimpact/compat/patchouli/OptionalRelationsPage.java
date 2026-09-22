package com.altnoir.flyimpact.compat.patchouli;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Like {@code patchouli:relations}, but skips entries hidden by flags (e.g. {@code mod:mekanism}).
 * Patchouli's built-in relations page throws if a listed chapter was never loaded.
 */
public final class OptionalRelationsPage extends PageWithText {
    List<String> entries;
    String title;

    transient List<BookEntry> entryObjs = List.of();

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);

        List<BookEntry> resolved = new ArrayList<>();
        if (this.entries != null) {
            for (String id : this.entries) {
                ResourceLocation targetId = ResourceLocation.tryParse(id);
                if (targetId == null) {
                    continue;
                }
                BookEntry targetEntry = builder.getEntry(targetId);
                if (targetEntry != null) {
                    resolved.add(targetEntry);
                }
            }
        }
        this.entryObjs = resolved;
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        List<BookEntry> displayedEntries = new ArrayList<>(this.entryObjs);
        displayedEntries.removeIf(BookEntry::shouldHide);
        Collections.sort(displayedEntries);
        for (int i = 0; i < displayedEntries.size(); i++) {
            addButton(new GuiButtonEntry(parent, 0, 20 + i * 11, displayedEntries.get(i), this::handleButtonEntry));
        }
    }

    public void handleButtonEntry(Button button) {
        GuiBookEntry.displayOrBookmark(parent, ((GuiButtonEntry) button).getEntry());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float pticks) {
        parent.drawCenteredStringNoShadow(
                graphics,
                title == null || title.isEmpty() ? I18n.get("patchouli.gui.lexicon.relations") : i18n(title),
                GuiBook.PAGE_WIDTH / 2,
                0,
                book.headerColor
        );
        GuiBook.drawSeparator(graphics, book, 0, 12);
        super.render(graphics, mouseX, mouseY, pticks);
    }

    @Override
    public int getTextHeight() {
        return 22 + this.entryObjs.size() * 11;
    }
}

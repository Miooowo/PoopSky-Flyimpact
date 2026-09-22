package com.altnoir.flyimpact.compat.jei;

import com.altnoir.flyimpact.menu.FlyimpactMenus;
import com.altnoir.flyimpact.menu.SimpleBreedingChestMenu;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.content.recipe.BreedingChestRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleBreedingChestTransferHandler implements IRecipeTransferHandler<SimpleBreedingChestMenu, RecipeHolder<BreedingChestRecipe>> {
    private static final int FLY_SLOT_1 = 1;
    private static final int FLY_SLOT_2 = 2;
    private static final int INVENTORY_START = 14;
    private static final int INVENTORY_END = 50;

    private final IRecipeTransferHandlerHelper transferHelper;

    public SimpleBreedingChestTransferHandler(IRecipeTransferHandlerHelper transferHelper) {
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<? extends SimpleBreedingChestMenu> getContainerClass() {
        return SimpleBreedingChestMenu.class;
    }

    @Override
    public Optional<MenuType<SimpleBreedingChestMenu>> getMenuType() {
        return Optional.of(FlyimpactMenus.SIMPLE_BREEDING_CHEST.get());
    }

    @Override
    public RecipeType<RecipeHolder<BreedingChestRecipe>> getRecipeType() {
        return com.altnoir.poopsky.compat.jei.BreedingChestRecipeCategory.TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            SimpleBreedingChestMenu container,
            RecipeHolder<BreedingChestRecipe> recipeHolder,
            IRecipeSlotsView recipeSlots,
            Player player,
            boolean maxTransfer,
            boolean doTransfer
    ) {
        BreedingChestRecipe recipe = recipeHolder.value();
        String[] needed = {recipe.parent1(), recipe.parent2()};
        List<IRecipeSlotView> inputViews = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        boolean[] slotUsed = new boolean[INVENTORY_END];
        int[] sourceSlots = new int[needed.length];
        List<IRecipeSlotView> missing = new ArrayList<>();

        for (int i = 0; i < needed.length; i++) {
            int alreadyInFlySlot = findFlySlot(container, needed[i], FLY_SLOT_1, FLY_SLOT_2 + 1, slotUsed);
            if (alreadyInFlySlot >= 0) {
                sourceSlots[i] = alreadyInFlySlot;
                slotUsed[alreadyInFlySlot] = true;
                continue;
            }

            int inventorySlot = findFlySlot(container, needed[i], INVENTORY_START, INVENTORY_END, slotUsed);
            if (inventorySlot >= 0) {
                sourceSlots[i] = inventorySlot;
                slotUsed[inventorySlot] = true;
            } else {
                sourceSlots[i] = -1;
                if (i < inputViews.size()) {
                    missing.add(inputViews.get(i));
                }
            }
        }

        if (!missing.isEmpty()) {
            return transferHelper.createUserErrorForMissingSlots(
                    Component.translatable("jei.flyimpact.breeding_chest.missing_flies"),
                    missing);
        }

        if (!doTransfer) {
            return null;
        }

        Minecraft minecraft = Minecraft.getInstance();
        MultiPlayerGameMode gameMode = minecraft.gameMode;
        if (gameMode == null) {
            return transferHelper.createInternalError();
        }

        boolean[] reservedFlySlots = new boolean[3];
        for (int i = 0; i < needed.length; i++) {
            if (sourceSlots[i] == FLY_SLOT_1 || sourceSlots[i] == FLY_SLOT_2) {
                reservedFlySlots[sourceSlots[i]] = true;
            }
        }

        for (int i = 0; i < needed.length; i++) {
            int source = sourceSlots[i];
            if (source == FLY_SLOT_1 || source == FLY_SLOT_2) {
                continue;
            }

            int destination = firstEmptyFlySlot(container, reservedFlySlots);
            if (destination < 0) {
                destination = firstDisplacedFlySlot(container, needed, reservedFlySlots);
            }
            if (destination < 0) {
                return transferHelper.createInternalError();
            }

            if (!container.getSlot(destination).getItem().isEmpty()) {
                gameMode.handleInventoryMouseClick(container.containerId, destination, 0, ClickType.QUICK_MOVE, player);
            }
            if (!container.getSlot(destination).getItem().isEmpty()) {
                return transferHelper.createInternalError();
            }

            gameMode.handleInventoryMouseClick(container.containerId, source, 0, ClickType.PICKUP, player);
            gameMode.handleInventoryMouseClick(container.containerId, destination, 0, ClickType.PICKUP, player);
            if (!player.containerMenu.getCarried().isEmpty()) {
                gameMode.handleInventoryMouseClick(container.containerId, source, 0, ClickType.PICKUP, player);
            }
            reservedFlySlots[destination] = true;
        }

        return null;
    }

    private static int firstEmptyFlySlot(SimpleBreedingChestMenu container, boolean[] reservedFlySlots) {
        for (int slot = FLY_SLOT_1; slot <= FLY_SLOT_2; slot++) {
            if (!reservedFlySlots[slot] && container.getSlot(slot).getItem().isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    private static int firstDisplacedFlySlot(SimpleBreedingChestMenu container, String[] needed, boolean[] reservedFlySlots) {
        for (int slot = FLY_SLOT_1; slot <= FLY_SLOT_2; slot++) {
            if (reservedFlySlots[slot]) {
                continue;
            }
            String type = flyType(container.getSlot(slot).getItem());
            if (type == null || !matchesAny(type, needed)) {
                return slot;
            }
        }
        return -1;
    }

    private static int findFlySlot(SimpleBreedingChestMenu container, String flyType, int start, int end, boolean[] used) {
        for (int i = start; i < end; i++) {
            if (used != null && used[i]) {
                continue;
            }
            Slot slot = container.getSlot(i);
            if (flyType.equals(flyType(slot.getItem()))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean matchesAny(String type, String[] needed) {
        for (String neededType : needed) {
            if (neededType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private static @Nullable String flyType(ItemStack stack) {
        if (!FlyItem.isFlyItem(stack)) {
            return null;
        }
        return FlyItem.getFlyType(stack).id();
    }
}

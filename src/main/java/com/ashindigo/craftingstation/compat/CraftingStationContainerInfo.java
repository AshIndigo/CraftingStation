package com.ashindigo.craftingstation.compat;

import com.ashindigo.craftingstation.handler.CraftingStationHandler;
import me.shedaniel.rei.server.ContainerInfo;
import me.shedaniel.rei.server.RecipeFinder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class CraftingStationContainerInfo implements ContainerInfo<CraftingStationHandler> {

    @Override
    public Class<? extends ScreenHandler> getContainerClass() {
        return CraftingStationHandler.class;
    }

    @Override
    public int getCraftingResultSlotIndex(CraftingStationHandler container) {
        return 0;
    }

    @Override
    public int getCraftingWidth(CraftingStationHandler container) {
        return 3;
    }

    @Override
    public int getCraftingHeight(CraftingStationHandler container) {
        return 3;
    }

    @Override
    public void clearCraftingSlots(CraftingStationHandler container) {
        container.craftingInventory.clear();
        container.resultInventory.clear();
    }

    @Override
    public void populateRecipeFinder(CraftingStationHandler container, RecipeFinder recipeFinder) {
        container.craftingInventory.provideRecipeInputs(new net.minecraft.recipe.RecipeFinder() {
            @Override
            public void addNormalItem(ItemStack itemStack_1) {
                recipeFinder.addNormalItem(itemStack_1);
            }
        });
    }
}

package com.ashindigo.craftingstation.compat;

import com.ashindigo.craftingstation.handler.CraftingStationContainer;
import me.shedaniel.rei.server.ContainerInfo;
import me.shedaniel.rei.server.RecipeFinder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class CraftingStationContainerInfo implements ContainerInfo<CraftingStationContainer> {

    @Override
    public Class<? extends ScreenHandler> getContainerClass() {
        return CraftingStationContainer.class;
    }

    @Override
    public int getCraftingResultSlotIndex(CraftingStationContainer container) {
        return 0;
    }

    @Override
    public int getCraftingWidth(CraftingStationContainer container) {
        return 3;
    }

    @Override
    public int getCraftingHeight(CraftingStationContainer container) {
        return 3;
    }

    @Override
    public void clearCraftingSlots(CraftingStationContainer container) {
        container.craftingInventory.clear();
        container.resultInventory.clear();
    }

    @Override
    public void populateRecipeFinder(CraftingStationContainer container, RecipeFinder recipeFinder) {
        container.craftingInventory.provideRecipeInputs(new net.minecraft.recipe.RecipeFinder() {
            @Override
            public void addNormalItem(ItemStack itemStack_1) {
                recipeFinder.addNormalItem(itemStack_1);
            }
        });
    }
}

package com.ashindigo.craftingstation;

import net.minecraft.container.Container;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.Iterator;

public class CraftingStationInventory extends CraftingInventory implements RecipeInputProvider, SidedInventory {

    private final DefaultedList<ItemStack> stacks;
    private Container container;

    public CraftingStationInventory(Container container_1, int int_1, int int_2) {
        super(container_1, int_1, int_2);
        this.stacks = DefaultedList.ofSize(int_1 * int_2, ItemStack.EMPTY);
        this.container = container_1;
    }

    @Override
    public int getInvSize() {
        return this.stacks.size();
    }

    @Override
    public boolean isInvEmpty() {
        Iterator var1 = this.stacks.iterator();
        if (!var1.hasNext()) {
            return true;
        }

        ItemStack itemStack_1 = (ItemStack) var1.next();
        while (itemStack_1.isEmpty()) {
            if (!var1.hasNext()) {
                return true;
            }
            itemStack_1 = (ItemStack) var1.next();
        }
        return false;
    }

    @Override
    public ItemStack getInvStack(int int_1) {
        return int_1 >= this.getInvSize() ? ItemStack.EMPTY : this.stacks.get(int_1);
    }

    @Override
    public ItemStack removeInvStack(int int_1) {
        return Inventories.removeStack(this.stacks, int_1);
    }

    @Override
    public ItemStack takeInvStack(int int_1, int int_2) {
        ItemStack itemStack_1 = Inventories.splitStack(this.stacks, int_1, int_2);
        if (!itemStack_1.isEmpty()) {
            this.container.onContentChanged(this);
        }
        return itemStack_1;
    }

    @Override
    public void setInvStack(int int_1, ItemStack itemStack_1) {
        this.stacks.set(int_1, itemStack_1);
        this.container.onContentChanged(this);
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    @Override
    public void provideRecipeInputs(RecipeFinder recipeFinder_1) {
        for (ItemStack itemStack_1 : this.stacks) {
            recipeFinder_1.addNormalItem(itemStack_1);
        }
    }

    public void setContainer(CraftingStationContainer craftingStationContainer) {
        this.container = craftingStationContainer;
    }

    @Override
    public int[] getInvAvailableSlots(Direction var1) {
        return new int[]{0, 1, 2, 3 ,4 ,5, 6, 7, 8};
    }

    @Override
    public boolean canInsertInvStack(int var1, ItemStack var2, @Nullable Direction var3) {
        return true;
    }

    @Override
    public boolean canExtractInvStack(int var1, ItemStack var2, Direction var3) {
        return true;
    }
}
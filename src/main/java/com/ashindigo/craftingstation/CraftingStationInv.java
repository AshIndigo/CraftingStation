package com.ashindigo.craftingstation;

import net.minecraft.container.Container;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CraftingStationInv extends CraftingInventory {
    private Container container;
    private final Inventory actualInv;

    public CraftingStationInv(Container container, Inventory actualInv) {
        super(container, 3, 3);
        this.container = container;
        this.actualInv = actualInv;
    }

    @Override
    public boolean isInvEmpty() {
        return actualInv.isInvEmpty();
    }

    @Override
    public ItemStack getInvStack(int index) {
        return actualInv.getInvStack(index);
    }

    @Override
    public ItemStack removeInvStack(int index) {
        return actualInv.removeInvStack(index);
    }

    @Override
    public ItemStack takeInvStack(int index, int count) {
        ItemStack removed = actualInv.takeInvStack(index, count);
        if (!removed.isEmpty()) {
            onCraftMatrixChanged();
        }
        return removed;
    }

    @Override
    public void setInvStack(int index, ItemStack stack) {
        actualInv.setInvStack(index, stack);
        onCraftMatrixChanged();
    }

    public void onCraftMatrixChanged() {
        this.container.onContentChanged(this);
    }

    @Override
    public void clear() {
        actualInv.clear();
    }
}

package com.ashindigo.craftingstation;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class CraftingStationInventory extends CraftingInventory {
    private final ScreenHandler container;
    private final Inventory actualInv;

    public CraftingStationInventory(ScreenHandler container, Inventory actualInv) {
        super(container, 3, 3);
        this.container = container;
        this.actualInv = actualInv;
    }

    @Override
    public boolean isEmpty() {
        return actualInv.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return actualInv.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return actualInv.removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack removed = actualInv.removeStack(slot, amount);
        if (!removed.isEmpty()) {
            onCraftMatrixChanged();
        }
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        actualInv.setStack(slot, stack);
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

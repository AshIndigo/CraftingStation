package com.ashindigo.craftingstation;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;

public class CraftingStationResultInventory extends CraftingResultInventory {

    @Override
    public boolean isValidInvStack(int int_1, ItemStack itemStack_1) {
        return false;
    }

}

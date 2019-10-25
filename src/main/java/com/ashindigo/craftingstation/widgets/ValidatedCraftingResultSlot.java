package com.ashindigo.craftingstation.widgets;

import com.ashindigo.craftingstation.CraftingStationInventory;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.DefaultedList;

/**
 * CraftingResultSlot but it's actually a ValidatedSlot
 */
@SuppressWarnings("WeakerAccess")
public class ValidatedCraftingResultSlot extends ValidatedSlot {

    private final CraftingInventory craftingInv;
    private final PlayerEntity player;
    private int amount;

    public ValidatedCraftingResultSlot(PlayerEntity playerEntity_1, CraftingStationInventory craftingInventory_1, Inventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.player = playerEntity_1;
        this.craftingInv = craftingInventory_1;
    }

    @Override
    public boolean canInsert(ItemStack itemStack_1) {
        return false;
    }

    @Override
    public ItemStack takeStack(int int_1) {
        if (this.hasStack()) {
            this.amount += Math.min(int_1, this.getStack().getCount());
        }

        return super.takeStack(int_1);
    }

    @Override
    protected void onCrafted(ItemStack itemStack_1, int int_1) {
        this.amount += int_1;
        this.onCrafted(itemStack_1);
    }

    @Override
    protected void onTake(int int_1) {
        this.amount += int_1;
    }

    @Override
    protected void onCrafted(ItemStack itemStack_1) {
        if (this.amount > 0) {
            itemStack_1.onCraft(this.player.world, this.player, this.amount);
        }

        if (this.inventory instanceof RecipeUnlocker) {
            ((RecipeUnlocker) this.inventory).unlockLastRecipe(this.player);
        }

        this.amount = 0;
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);
        DefaultedList<ItemStack> defaultedList_1 = player.world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, this.craftingInv, player.world);

        for (int int_1 = 0; int_1 < defaultedList_1.size(); ++int_1) {
            ItemStack itemStack_2 = this.craftingInv.getInvStack(int_1);
            ItemStack itemStack_3 = defaultedList_1.get(int_1);
            if (!itemStack_2.isEmpty()) {
                this.craftingInv.takeInvStack(int_1, 1);
                itemStack_2 = this.craftingInv.getInvStack(int_1);
            }

            if (!itemStack_3.isEmpty()) {
                if (itemStack_2.isEmpty()) {
                    this.craftingInv.setInvStack(int_1, itemStack_3);
                } else if (ItemStack.areItemsEqualIgnoreDamage(itemStack_2, itemStack_3) && ItemStack.areTagsEqual(itemStack_2, itemStack_3)) {
                    itemStack_3.increment(itemStack_2.getCount());
                    this.craftingInv.setInvStack(int_1, itemStack_3);
                } else if (!this.player.inventory.insertStack(itemStack_3)) {
                    this.player.dropItem(itemStack_3, false);
                }
            }
        }

        return stack;
    }
}

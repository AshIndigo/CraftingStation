package com.ashindigo.craftingstation;

import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class CraftingStationContainer extends CraftingContainer<CraftingStationInventory> {

    private final PlayerEntity player;
    private CraftingStationInventory inventory;
    private final CraftingResultInventory resultInv;
    private final BlockContext context;

    public CraftingStationContainer(int sync, PlayerEntity player, CraftingStationInventory inventory, CraftingResultInventory resultInv, BlockContext context) {
        super(null, sync);
        this.player = player;
        this.inventory = inventory;
        this.resultInv = resultInv;
        this.context = context;
        this.addSlot(new CraftingResultSlot(player, this.inventory, this.resultInv, 0, 124, 35));
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(inventory, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
        onContentChanged(inventory);
    }

    @Override
    public void populateRecipeFinder(RecipeFinder recipeFinder_1) {
        this.inventory.provideRecipeInputs(recipeFinder_1);
    }

    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
        this.resultInv.clear();
    }

    @Override
    public void onContentChanged(Inventory inventory_1) {
        this.context.run((world, blockPos_1) -> {
            if (!world.isClient) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
                ItemStack stack = ItemStack.EMPTY;

                Optional<CraftingRecipe> opt = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, world);
                if (opt.isPresent()) {
                    CraftingRecipe craftingRecipe_1 = opt.get();
                    if (resultInv.shouldCraftRecipe(world, serverPlayer, craftingRecipe_1)) {
                        stack = craftingRecipe_1.craft(inventory);
                    }
                }
                resultInv.setInvStack(0, stack);
                serverPlayer.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, 0, stack));
            }
            world.getBlockEntity(blockPos_1).markDirty();
        });
    }

    @Override
    public boolean matches(Recipe<? super CraftingStationInventory> var1) {
        return var1.matches(this.inventory, this.player.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return 3;
    }

    @Override
    public int getCraftingHeight() {
        return 3;
    }

    @Override
    public int getCraftingSlotCount() {
        return 10;
    }

    @Override
    public boolean canUse(PlayerEntity var1) {
        return true;
    }

}

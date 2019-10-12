package com.ashindigo.craftingstation.gui;

import com.ashindigo.craftingstation.CraftingStationResultInventory;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.Optional;

public class CraftingStationGui extends CottonScreenController {

    private final CraftingStationResultInventory resultInv;
    private final BlockContext context;

    public CraftingStationGui(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        this.context = context;
        WGridPanel rootPanel = (WGridPanel) getRootPanel();
        rootPanel.add(new WLabel(new TranslatableText("container.craftingstation.craftingstation"), WLabel.DEFAULT_TEXT_COLOR), 2, 0);
        this.resultInv = new CraftingStationResultInventory();
        WItemSlot slot = WItemSlot.of(blockInventory, 0, 3, 3);
        rootPanel.add(slot, 2, 1);
        WItemSlot output = WItemSlot.outputOf(resultInv, 9);
        rootPanel.add(output, 7, 2);
        WPlayerInvPanel invPanel = this.createPlayerInventoryPanel();
        rootPanel.add(invPanel, 0, 5);
        rootPanel.validate(this);
    }

    @Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
        ItemStack stack = super.onSlotClick(slotNumber, button, action, player);
        onContentChanged(this.blockInventory);
        return stack;
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
    @Environment(EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 9;
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 9;
    }

    protected void updateResult(int int_1, World world, PlayerEntity playerEntity, CraftingInventory craftingInv, CraftingResultInventory resultInv) {
        if (!world.isClient) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)playerEntity;
            ItemStack stack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInv, world);
            if (optional.isPresent()) {
                CraftingRecipe craftingRecipe_1 = optional.get();
                if (resultInv.shouldCraftRecipe(world, serverPlayer, craftingRecipe_1)) {
                    stack = craftingRecipe_1.craft(craftingInv);
                }
            }

            resultInv.setInvStack(getCraftingResultSlotIndex(), stack);
            serverPlayer.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(int_1, getCraftingResultSlotIndex(), stack));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory_1) {
        this.context.run((world_1, blockPos_1) -> {
            updateResult(this.syncId, world_1, playerInventory.player, (CraftingInventory) this.blockInventory, this.resultInv);
        });
    }
}

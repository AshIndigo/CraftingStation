package com.ashindigo.craftingstation.gui;

import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;

public class CraftingStationGui extends CottonScreenController {

    public CraftingStationGui(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(RecipeType.CRAFTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        WGridPanel rootPanel = (WGridPanel) getRootPanel();

        rootPanel.add(new WLabel(new TranslatableText("container.craftingstation.craftingstation"), WLabel.DEFAULT_TEXT_COLOR), 0, 0);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                WItemSlot slot = WItemSlot.of(blockInventory, i * j);
                rootPanel.add(slot, 4 + i, 1 + j);
            }
        }

        WItemSlot output = WItemSlot.of(blockInventory, 9);
        rootPanel.add(output, 10, 2);
        rootPanel.add(this.createPlayerInventoryPanel(), 0, 3);
        rootPanel.validate(this);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 9;
    }
}

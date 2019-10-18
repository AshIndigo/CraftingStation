package com.ashindigo.craftingstation.widgets;

import io.github.cottonmc.cotton.gui.ValidatedSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;

public class ValidatedListSlot extends ValidatedSlot {

    public ValidatedListSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    //@Environment(EnvType.CLIENT)
    @Override
    public boolean doDrawHoveringEffect() {
        return this.yPosition <= 162 && this.yPosition > 0; // && xPosition < 154 && xPosition > 0;
    }

}

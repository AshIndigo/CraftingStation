package com.ashindigo.craftingstation.widgets;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.inventory.Inventory;

public class WListItemSlot extends WItemSlot {
    private final Inventory inventory;
    private final int startIndex;
    private final int slotsWide;
    private final int slotsHigh;

    public WListItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
        super(inventory, startIndex, slotsWide, slotsHigh, big, false);
        this.inventory = inventory;
        this.startIndex = startIndex;
        this.slotsWide = slotsWide;
        this.slotsHigh = slotsHigh;
    }

    @Override
    public void createPeers(GuiDescription c) {
        int index = startIndex;
        for (int y = 0; y < slotsHigh; y++) {
            for (int x = 0; x < slotsWide; x++) {
                ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18)) {
                    @Override
                    public boolean doDrawHoveringEffect() {
                        return this.yPosition <= 162 && this.yPosition > 0;
                    }
                };
                c.addSlotPeer(slot);
                index++;
            }
        }
    }
}

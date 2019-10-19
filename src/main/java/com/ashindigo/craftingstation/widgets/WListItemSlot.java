package com.ashindigo.craftingstation.widgets;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;

import java.util.List;

public class WListItemSlot extends WItemSlot {
    private final List<Slot> peers = Lists.newArrayList();
    private Inventory inventory;
    private int startIndex;
    private int slotsWide;
    private int slotsHigh;

    public WListItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
        super(inventory, startIndex, slotsWide, slotsHigh, big, false);
        this.inventory = inventory;
        this.startIndex = startIndex;
        this.slotsWide = slotsWide;
        this.slotsHigh = slotsHigh;
    }

    @Override
    public void createPeers(GuiDescription c) {
        peers.clear();
        int index = startIndex;
        for (int y = 0; y < slotsHigh; y++) {
            for (int x = 0; x < slotsWide; x++) {
                ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18)) {
                    @Override
                    public boolean doDrawHoveringEffect() {
                        return this.yPosition <= 162 && this.yPosition > 0;
                    }
                };
                peers.add(slot);
                c.addSlotPeer(slot);
                index++;
            }
        }
    }
}

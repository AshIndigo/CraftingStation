package com.ashindigo.craftingstation.widgets;

import com.ashindigo.craftingstation.CraftingStationInventory;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

/**
 * Custom WItemSlot to change the slot to my own
 */
public class WCraftingResultSlot extends WItemSlot {

    private final int startIndex;
    private final int slotsWide;
    private final int slotsHigh;
    private final PlayerEntity playerEntity_1;
    private final CraftingStationInventory craftingInventory_1;
    private final Inventory inventory;

    public WCraftingResultSlot(PlayerEntity playerEntity_1, CraftingStationInventory craftingInventory_1, Inventory resultInv, int startIndex, int slotsWide, int slotsHigh, boolean big, boolean ltr) {
        super(resultInv, startIndex, slotsWide, slotsHigh, big, ltr);
        this.playerEntity_1 = playerEntity_1;
        this.craftingInventory_1 = craftingInventory_1;
        this.inventory = resultInv;
        this.startIndex = startIndex;
        this.slotsWide = slotsWide;
        this.slotsHigh = slotsHigh;
    }

    @Override
    public void createPeers(GuiDescription c) {
        int index = startIndex;
        for (int y = 0; y < slotsHigh; y++) {
            for (int x = 0; x < slotsWide; x++) {
                ValidatedCraftingResultSlot slot = new ValidatedCraftingResultSlot(playerEntity_1, craftingInventory_1, inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18));
                c.addSlotPeer(slot);
                index++;
            }
        }
    }
}

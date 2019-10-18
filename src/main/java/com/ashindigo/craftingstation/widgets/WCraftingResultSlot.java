package com.ashindigo.craftingstation.widgets;

import com.ashindigo.craftingstation.CraftingStationInventory;
import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

import java.util.List;

/**
 * Custom WItemSlot to change the slot to my own
 */
public class WCraftingResultSlot extends WItemSlot {

    private final List<Slot> peers = Lists.newArrayList();
    private int startIndex;
    private int slotsWide;
    private int slotsHigh;
    private final PlayerEntity playerEntity_1;
    private final CraftingStationInventory craftingInventory_1;
    private Inventory inventory;

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
        peers.clear();
        int index = startIndex;
        for (int y = 0; y < slotsHigh; y++) {
            for (int x = 0; x < slotsWide; x++) {
                ValidatedCraftingResultSlot slot = new ValidatedCraftingResultSlot(playerEntity_1, craftingInventory_1, inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18));
                peers.add(slot);
                c.addSlotPeer(slot);
                index++;
            }
        }
    }
}

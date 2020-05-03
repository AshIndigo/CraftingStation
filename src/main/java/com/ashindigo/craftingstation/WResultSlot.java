package com.ashindigo.craftingstation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import spinnery.common.BaseContainer;
import spinnery.widget.WSlot;
import spinnery.widget.api.Action;

import static net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE;
import static spinnery.registry.NetworkRegistry.*;
import static spinnery.registry.NetworkRegistry.createSlotDragPacket;
import static spinnery.util.MouseUtilities.*;
import static spinnery.widget.api.Action.*;
import static spinnery.widget.api.Action.CLONE;

public class WResultSlot extends WSlot {
	public WResultSlot() {
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseReleased(int mouseX, int mouseY, int button) {
		if (button == MIDDLE || isLocked()) return;

		PlayerEntity player = getInterface().getContainer().getPlayerInventory().player;
		BaseContainer container = getInterface().getContainer();

		boolean isCursorEmpty = player.inventory.getCursorStack().isEmpty();

		if (!Screen.hasShiftDown()) {
			if (!isFocused()) {
				return;
			} else if ((button == LEFT || button == RIGHT) && !isCursorEmpty) {
				container.onSlotAction(slotNumber, inventoryNumber, button, PICKUP, player);
				INSTANCE.sendToServer(SLOT_CLICK_PACKET, createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, PICKUP));
			}
		}

		container.flush();

		skipRelease = false;

		super.onMouseReleased(mouseX, mouseY, button);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseClicked(int mouseX, int mouseY, int button) {
		if (!isFocused() || isLocked()) return;

		PlayerEntity player = getInterface().getContainer().getPlayerInventory().player;
		BaseContainer container = getInterface().getContainer();

		boolean isCursorEmpty = player.inventory.getCursorStack().isEmpty();

		if (Screen.hasShiftDown()) {
			if (button == LEFT) {
				getInterface().getCachedWidgets().put(getClass(), this);
				container.onSlotAction(slotNumber, inventoryNumber, button, QUICK_MOVE, player);
				INSTANCE.sendToServer(SLOT_CLICK_PACKET, createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, QUICK_MOVE));
			}
		} else {
			if ((button == LEFT || button == RIGHT) && isCursorEmpty) {
				skipRelease = true;
				container.onSlotAction(slotNumber, inventoryNumber, button, PICKUP, player);
				INSTANCE.sendToServer(SLOT_CLICK_PACKET, createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, PICKUP));
			} else if (button == MIDDLE) {
				container.onSlotAction(slotNumber, inventoryNumber, button, CLONE, player);
				INSTANCE.sendToServer(SLOT_CLICK_PACKET, createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, CLONE));
			}
		}
	}
}

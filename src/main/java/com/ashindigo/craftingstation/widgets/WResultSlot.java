package com.ashindigo.craftingstation.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.common.registry.NetworkRegistry;
import spinnery.widget.WSlot;
import spinnery.widget.api.Action;

import static net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE;

public class WResultSlot extends WSlot {
	public WResultSlot() {
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseReleased(float mouseX, float mouseY, int button) {
		if (button == MIDDLE || isLocked()) return;

		PlayerEntity player = getInterface().getHandler().getPlayerInventory().player;
		BaseScreenHandler container = getInterface().getHandler();

		boolean isCursorEmpty = player.inventory.getCursorStack().isEmpty();

		if (!Screen.hasShiftDown()) {
			if (!isFocused()) {
				return;
			} else if ((button == LEFT || button == RIGHT) && !isCursorEmpty) {
				container.onSlotAction(slotNumber, inventoryNumber, button, Action.PICKUP, player);
				INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, Action.PICKUP));
			}
		}

		container.flush();

		skipRelease = false;

		super.onMouseReleased(mouseX, mouseY, button);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void onMouseClicked(float mouseX, float mouseY, int button) {
		if (!isFocused() || isLocked()) return;

		PlayerEntity player = getInterface().getHandler().getPlayerInventory().player;
		BaseScreenHandler container = getInterface().getHandler();

		boolean isCursorEmpty = player.inventory.getCursorStack().isEmpty();

		if (Screen.hasShiftDown()) {
			if (button == LEFT) {
				getInterface().getCachedWidgets().put(getClass(), this);
				container.onSlotAction(slotNumber, inventoryNumber, button, Action.QUICK_MOVE, player);
				INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, Action.QUICK_MOVE));
			}
		} else {
			if ((button == LEFT || button == RIGHT) && isCursorEmpty) {
				skipRelease = true;
				container.onSlotAction(slotNumber, inventoryNumber, button, Action.PICKUP, player);
				INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, Action.PICKUP));
			} else if (button == MIDDLE) {
				container.onSlotAction(slotNumber, inventoryNumber, button, Action.CLONE, player);
				INSTANCE.sendToServer(NetworkRegistry.SLOT_CLICK_PACKET, NetworkRegistry.createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, Action.CLONE));
			}
		}
	}
}

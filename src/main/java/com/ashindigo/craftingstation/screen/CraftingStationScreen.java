package com.ashindigo.craftingstation.screen;

import com.ashindigo.craftingstation.CraftingStation;
import com.ashindigo.craftingstation.entity.CraftingStationEntity;
import com.ashindigo.craftingstation.handler.CraftingStationContainer;
import com.ashindigo.craftingstation.widgets.WResultSlot;
import com.ashindigo.craftingstation.widgets.WVerticalScrollableContainerModified;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.ashindigo.craftingstation.handler.CraftingStationContainer.INVENTORY;

public class CraftingStationScreen extends BaseHandledScreen<CraftingStationContainer> {

    public CraftingStationScreen(CraftingStationContainer linkedContainer, PlayerInventory playerInv, Text name) {
        super(name, linkedContainer, playerInv.player);
        PlayerEntity player = playerInv.player;
        WInterface mainInterface = getInterface();
        WPanel mainPanel = mainInterface.createChild(WPanel::new, Position.of(mainInterface), Size.of(176, 166));

        mainPanel.setOnAlign(WAbstractWidget::center);
        mainPanel.setLabel(name);

        Size size = Size.of(18, 18);
        Position position = Position.of(mainPanel, 24, 24, 1);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                mainPanel.createChild(WSlot::new, position.add(size.getWidth() * x, size.getHeight() * y, 1), size).setSlotNumber(x + y * 3).setInventoryNumber(INVENTORY);
            }
        }
        mainPanel.createChild(WStaticImage::new, Position.of(mainPanel, 114 - 30, 43, 1), Size.of(22, 15)).setTexture(new Identifier(CraftingStation.MODID, "gui/arrow.png"));
        mainPanel.createChild(WResultSlot::new, Position.of(mainPanel, 114, 42, 1), Size.of(18, 18)).setInventoryNumber(3).setSlotNumber(0);

        WSlot.addPlayerInventory(Position.of(mainPanel).add(7, 83, 1), Size.of(18, 18), mainPanel);

        for (Direction direction : Direction.values()) {
            ScreenHandlerContext context = ScreenHandlerContext.create(linkedContainer.getWorld(), linkedContainer.craftingStationEntity.getPos());
            Optional<BlockPos> optional = context.run((world, blockPos) -> {
                return blockPos.offset(direction);
            });
            if (optional.isPresent()) {
                BlockEntity blockEntity = player.world.getBlockEntity(optional.get());
                if (blockEntity != null && !(blockEntity instanceof CraftingStationEntity)) {
                    if (player.world.getBlockState(optional.get()).getBlock() instanceof ChestBlock) {
                        addAttachedInventory(ChestBlock.getInventory((ChestBlock) player.world.getBlockState(optional.get()).getBlock(), player.world.getBlockState(optional.get()), player.world, optional.get(), true), mainPanel, mainInterface);
                        break;
                    }
                    if (playerInventory.player.world.getBlockState(optional.get()).getBlock() instanceof InventoryProvider) {
                        addAttachedInventory(((InventoryProvider) playerInventory.player.world.getBlockState(optional.get()).getBlock()).getInventory(player.world.getBlockState(optional.get()), player.world, optional.get()), mainPanel, mainInterface);
                        break;
                    }
                    if (blockEntity instanceof Inventory) {
                        addAttachedInventory((Inventory) blockEntity, mainPanel, mainInterface);
                        break;
                    }
                }
            }
        }

        mainPanel.center();
    }

    void addAttachedInventory(Inventory inv, WPanel mainPanel, WInterface mainInterface) {
        Size slotSize = Size.of(18, 18);
        WVerticalScrollableContainerModified list = mainInterface.createChild(WVerticalScrollableContainerModified::new, Position.of(mainPanel).add(3, 22, 1), Size.of(104, 166 - 36)).setDivisionSpace(0);
        mainPanel.setSize(Size.of(181 + (5 * 18) + 9, 166));
        for (WAbstractWidget widget : mainPanel.getWidgets()) {
            widget.setPosition(widget.getPosition().add((5 * 18) + 15, 0, 0));
        }
        int c = 0;
        int y = 0;
        ArrayList<WSlot> row = new ArrayList<>(Collections.nCopies(9, null));
        for (int i = 0; i < inv.size(); i++) {
            if (inv.isValid(i, inv.getStack(i))) {
                WSlot slot = new WSlot().setInventoryNumber(2).setSlotNumber(i).setPosition(Position.of(list).add((18 * c), 0, 2).setOffsetY((18 * y))).setSize(slotSize);
                if (!row.isEmpty()) {
                    row.set(c, slot);
                }
                if (c == 4) {
                    y++;
                    row.removeIf(Objects::isNull);
                    if (!list.contains(row.toArray(new WSlot[]{}))) {
                        list.addRow(row.toArray(new WSlot[]{}));
                        row = new ArrayList<>(Collections.nCopies(9, null));
                    }
                }
                c = c == 4 ? 0 : c + 1;
            }
        }
        row.removeIf(Objects::isNull);
        if (!list.contains(row.toArray(new WSlot[]{}))) {
            list.addRow(row.toArray(new WSlot[]{}));
        }
    }
}
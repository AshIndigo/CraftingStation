package com.ashindigo.craftingstation;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import spinnery.common.BaseContainerScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.Optional;

import static com.ashindigo.craftingstation.CraftingStationContainer.INVENTORY;

public class CraftingStationScreen extends BaseContainerScreen<CraftingStationContainer> {

    public CraftingStationScreen(Text text, CraftingStationContainer linkedContainer, PlayerEntity player, int arrayWidth, int arrayHeight) { // arrayWidth is size
        super(text, linkedContainer, player);

        WInterface mainInterface = getInterface();
        WPanel mainPanel = mainInterface.createChild(WPanel::new, Position.of(mainInterface), Size.of(176, 166));

        mainPanel.setOnAlign(WAbstractWidget::center);
        mainPanel.setLabel(text);

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
            BlockContext context = BlockContext.create(linkedContainer.getWorld(), linkedContainer.craftingStationEntity.getPos());
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
                        addAttachedInventory(((InventoryProvider) blockEntity).getInventory(player.world.getBlockState(optional.get()), player.world, optional.get()), mainPanel, mainInterface);
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
        WVerticalScrollableContainer list = mainInterface.createChild(WVerticalScrollableContainer::new, Position.of(mainPanel).add(3, 22, 1), Size.of(104, 166 - 36));
        mainPanel.setSize(Size.of(181 + (5 * 18) + 9, 166));
        for (WAbstractWidget widget : mainPanel.getWidgets()) {
            widget.setPosition(widget.getPosition().add((5 * 18) + 15, 0, 0));
        }
        list.createChild(WStaticText::new, Position.of(list, 24, 0, 2), Size.of(30, 18)).setText("Inventory");
        int finalY = 0;
        for (int i = 0; i < inv.getInvSize() / 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (inv.isValidInvStack(i, inv.getInvStack(i)))
                list.createChild(WSlot::new, Position.of(list, 4 + (18 * j), 18 + (18 * i), 2), Size.of(18, 18)).setInventoryNumber(2).setSlotNumber((5 * i) + j);
                finalY = 18 + (18 * i);
            }
        }
        for (int i = 0; i < inv.getInvSize() % 5; i++) {
            if (inv.isValidInvStack(i, inv.getInvStack(i)))
            list.createChild(WSlot::new, Position.of(list, 4 + (18 * i), finalY + 18, 2), Size.of(18, 18)).setInventoryNumber(2).setSlotNumber((inv.getInvSize() / 5) * 5 + i);
        }
    }
}
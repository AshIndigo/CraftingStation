package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.widgets.WCraftingResultSlot;
import com.ashindigo.craftingstation.widgets.WItemListPanel;
import com.ashindigo.craftingstation.widgets.WListItemSlot;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Optional;

public class CraftingStationContainer extends CottonScreenController {
    private final PlayerEntity player;
    private final CraftingStationInventory inventory;
    private final CraftingResultInventory resultInv;
    private final BlockContext context;
    private Inventory inv;

    public CraftingStationContainer(int sync, PlayerEntity player, CraftingStationInventory inventory, CraftingResultInventory resultInv, BlockContext context) {
        super(RecipeType.CRAFTING, sync, player.inventory, inventory, null);
        this.player = player;
        this.inventory = inventory;
        this.resultInv = resultInv;
        this.context = context;
        WGridPanel rootPanel = (WGridPanel) getRootPanel();
        int offsetX = 0;
        for (Direction dir : Direction.values()) {
            Optional<BlockPos> opt = context.run(((world, blockPos) -> {
                return blockPos.offset(dir);
            }));
            if (opt.isPresent()) {
                BlockEntity te = player.world.getBlockEntity(opt.get());
                if (te != null && !(te instanceof CraftingStationTileEntity)) {
                    if (player.world.getBlockState(opt.get()).getBlock() instanceof ChestBlock) {
                        offsetX = 4;
                        inv = ChestBlock.getInventory(player.world.getBlockState(opt.get()), player.world, opt.get(), true);
                        ArrayList<WListItemSlot> defList = new ArrayList<>();
                        if (inv != null) {
                            for (int i = 0; i < inv.getInvSize() / 3; i++) {
                                defList.add(new WListItemSlot(inv, i * 3, 3, 1, false));
                            }
                            rootPanel.add(new WItemListPanel(defList, this), 0, 0);
                            break;
                        }
                    }
                    if (te instanceof InventoryProvider) {
                        offsetX = 4;
                        inv = ((InventoryProvider) te).getInventory(player.world.getBlockState(opt.get()), player.world, opt.get());
                        ArrayList<WListItemSlot> defList = new ArrayList<>();
                        for (int i = 0; i < inv.getInvSize() / 3; i++) {
                            defList.add(new WListItemSlot(inv, i * 3, 3, 1, false));
                        }
                        rootPanel.add(new WItemListPanel(defList, this), 0, 0);
                        break;
                    } else if (te instanceof Inventory) {
                        offsetX = 4;
                        inv = (Inventory) te;
                        ArrayList<WListItemSlot> defList = new ArrayList<>();
                        for (int i = 0; i < inv.getInvSize() / 3; i++) {
                            defList.add(new WListItemSlot(inv, i * 3, 3, 1, false));
                        }
                        rootPanel.add(new WItemListPanel(defList, this), 0, 0);
                        break;
                    }
                }
            }
        }
        rootPanel.add(new WLabel(new TranslatableText("container.craftingstation.craftingstation"), 0x404040), 1 + offsetX, 0);
        rootPanel.add(new WCraftingResultSlot(player, inventory, resultInv, 0, 1, 1, true, false), 7 + offsetX, 2);
        rootPanel.add(WItemSlot.of(inventory, 0, 3, 3), 1 + offsetX, 1);
        // Arrow
        rootPanel.add(new WSprite(new Identifier(CraftingStation.MODID, "textures/gui/arrow1.png")), 5 + offsetX, 2);
        rootPanel.add(new WSprite(new Identifier(CraftingStation.MODID, "textures/gui/arrow2.png")), 6 + offsetX, 2);
        // Player panel
        rootPanel.add(this.createPlayerInventoryPanel(), offsetX, 5);
        this.clearSlots();
        this.triggerValidatation();
        onContentChanged(inventory);
    }

    public void triggerValidatation() {
        rootPanel.validate(this);
        onContentChanged(inventory);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity_1, int int_1) { // + 10
        ItemStack itemStack_1 = ItemStack.EMPTY;
        Slot slot_1 = this.slotList.get(int_1);
        if (slot_1 != null && slot_1.hasStack()) {
            ItemStack itemStack_2 = slot_1.getStack();
            itemStack_1 = itemStack_2.copy();
            if (int_1 == 0) {
                this.context.run((world_1, blockPos_1) -> {
                    itemStack_2.getItem().onCraft(itemStack_2, world_1, playerEntity_1);
                });
                if (!this.insertItem(itemStack_2, 0, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot_1.onStackChanged(itemStack_2, itemStack_1);
            } else if (int_1 >= 10 && int_1 < 37) {
                if (!this.insertItem(itemStack_2, 27, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (int_1 >= 37 && int_1 < 46) {
                if (!this.insertItem(itemStack_2, 0, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack_2, 0, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack_2.isEmpty()) {
                slot_1.setStack(ItemStack.EMPTY);
            } else {
                slot_1.markDirty();
            }

            if (itemStack_2.getCount() == itemStack_1.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemStack_3 = slot_1.onTakeItem(playerEntity_1, itemStack_2);
            if (int_1 == 0) {
                playerEntity_1.dropItem(itemStack_3, false);
            }
        }

        return itemStack_1;
    }

    @Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
        if (action == SlotActionType.QUICK_MOVE) {
            return transferSlot(player, slotNumber);
        } else {
            return super.onSlotClick(slotNumber, button, action, player);
        }
    }


    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
        this.resultInv.clear();
    }

    @Override
    public void onContentChanged(Inventory inventory_1) {
        this.context.run((world, blockPos_1) -> {
            if (!world.isClient) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                ItemStack stack = ItemStack.EMPTY;

                Optional<CraftingRecipe> opt = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, world);
                if (opt.isPresent()) {
                    CraftingRecipe craftingRecipe_1 = opt.get();
                    if (resultInv.shouldCraftRecipe(world, serverPlayer, craftingRecipe_1)) {
                        stack = craftingRecipe_1.craft(inventory);
                    }
                }
                resultInv.setInvStack(0, stack);
                serverPlayer.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, inv != null ? inv.getInvSize() : 0, stack));
            }
            world.getBlockEntity(blockPos_1).markDirty();
        });
    }

    public void clearSlots() {
        this.slotList.clear();
    }

    @Override
    public boolean matches(Recipe<? super Inventory> var1) {
        return var1.matches(this.inventory, this.player.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return 3;
    }

    @Override
    public int getCraftingHeight() {
        return 3;
    }

    @Override
    public int getCraftingSlotCount() {
        return 10;
    }


}

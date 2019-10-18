package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.widgets.WCraftingResultSlot;
import com.ashindigo.craftingstation.widgets.WItemListPanel;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
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
    private int offsetX = 0;

    public CraftingStationContainer(int sync, PlayerEntity player, CraftingStationInventory inventory, CraftingResultInventory resultInv, BlockContext context) {
        super(RecipeType.CRAFTING, sync, player.inventory, inventory, null);
        this.player = player;
        this.inventory = inventory;
        this.resultInv = resultInv;
        this.context = context;
        WGridPanel rootPanel = (WGridPanel) getRootPanel();
        for (Direction dir : Direction.values()) {
            Optional<BlockPos> opt = context.run(((world, blockPos) -> {
                return blockPos.offset(dir);
            }));
            if (opt.isPresent()) {
                BlockEntity te = player.world.getBlockEntity(opt.get());
                if (te != null && !(te instanceof CraftingStationTileEntity)) {
                    Inventory inv;
                    if (player.world.getBlockState(opt.get()).getBlock() instanceof ChestBlock) {
                        offsetX = 4;
                        inv = ChestBlock.getInventory(player.world.getBlockState(opt.get()), player.world, opt.get(), true);
                        ArrayList<WItemSlot> defList = new ArrayList<>();
                        for (int i = 0; i < inv.getInvSize() / 3; i++) {
                            defList.add(WItemSlot.of(inv, i * 3, 3, 1));
                        }
                        rootPanel.add(new WItemListPanel(defList, this), 0,0);
                        break;
                    }
                    if (te instanceof InventoryProvider) {
                        offsetX = 4;
                        inv = ((InventoryProvider) te).getInventory(player.world.getBlockState(opt.get()), player.world, opt.get());
                        rootPanel.add(WItemSlot.of(inv, 0, 3, inv.getInvSize() / 3), 0, 0);
                        break;
                    } else if (te instanceof Inventory) {
                        offsetX = 4;
                        inv = (Inventory) te;
                        rootPanel.add(WItemSlot.of(inv, 0, 3, (inv.getInvSize()) / 3), 0, 0);
                        break;
                    }
                }
            }
        }
        rootPanel.add(new WLabel(new TranslatableText("container.craftingstation.craftingstation"), 0x404040), 1+ offsetX, 0);
        rootPanel.add(new WCraftingResultSlot(player, inventory, resultInv, 0, 1, 1, true, false), 7+ offsetX, 2);
        rootPanel.add(WItemSlot.of(inventory, 0, 3, 3), 1 + offsetX, 1);
        // Arrow
        rootPanel.add(new WSprite(new Identifier(CraftingStation.MODID, "textures/gui/arrow1.png")), 5 + offsetX, 2);
        rootPanel.add(new WSprite(new Identifier(CraftingStation.MODID, "textures/gui/arrow2.png")), 6 + offsetX, 2);
        // Player panel
        rootPanel.add(this.createPlayerInventoryPanel(), offsetX, 5);
        this.triggerValidatation();
        onContentChanged(inventory);
    }

    public void triggerValidatation() {
        rootPanel.validate(this);
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
                serverPlayer.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, 0, stack));
            }
            world.getBlockEntity(blockPos_1).markDirty();
        });
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

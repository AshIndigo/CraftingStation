package com.ashindigo.craftingstation;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spinnery.common.BaseContainer;
import spinnery.registry.NetworkRegistry;
import spinnery.util.StackUtilities;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;
import spinnery.widget.api.Action;

import java.util.Optional;
import java.util.function.BiFunction;

public class CraftingStationContainer extends BaseContainer { // Mess of a class, just run away

    public static final int INVENTORY = 1;
    CraftingStationEntity craftingStationEntity;
    CraftingResultInventory resultInventory;
    CraftingStationInv craftInv;

    public CraftingStationContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos, int arrayWidth, int arrayHeight, int m) {
        super(synchronizationID, playerInventory);
        craftingStationEntity = ((CraftingStationEntity) getWorld().getBlockEntity(pos));
        WInterface mainInterface = getInterface();
        getInventories().put(INVENTORY, craftingStationEntity.inventory);
        craftInv = new CraftingStationInv(this, craftingStationEntity.inventory);
        craftingStationEntity.inventory.addListener(this::onContentChanged);
        WSlot.addHeadlessPlayerInventory(mainInterface);
        // Add the base slots for crafting
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                WSlot wslot = mainInterface.createChild(WSlot::new).setSlotNumber(x + y * 3).setInventoryNumber(INVENTORY);
            }
        }

        // If the result slot's inventory non existant than make it
        if (resultInventory == null) {
            resultInventory = new CraftingResultInventory() {
                @Override
                public void setInvStack(int slot, ItemStack stack) {
                    super.setInvStack(slot, stack);

                    if (!world.isClient) {
                        if (!craftingStationEntity.inventory.isInvEmpty() && world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftInv, world).isPresent() && !stack.isItemEqual(getInvStack(0))) {
                            onContentChanged(null);
                        }
                    }
                    if (craftingStationEntity.inventory.isInvEmpty()) {
                        super.setInvStack(0, ItemStack.EMPTY);
                    }
                }
            };
        }
        getInventories().put(3, resultInventory);
        WSlot resultSlot = mainInterface.createChild(WSlot::new).setSlotNumber(0).setInventoryNumber(3);
        // No insertion, only take
        resultSlot.accept(ItemStack.EMPTY.getItem());
        resultSlot.setWhitelist();
        // Trigger the crafting using a vanilla inv slot.
        resultSlot.addConsumer((action, subtype) -> {
            if (action == Action.PICKUP || action == Action.PICKUP_ALL || action == Action.QUICK_MOVE) {
                if (subtype == Action.Subtype.FROM_CURSOR_TO_SLOT_DEFAULT_FULL_STACK || subtype == Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM || subtype == Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK || subtype == Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK || subtype == Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK) {
                    if (!resultSlot.getStack().isEmpty()) {
                        if (!world.isClient)
                        new CraftingResultSlot(playerInventory.player, craftInv, resultInventory, 0, 0, 0).onTakeItem(playerInventory.player, resultSlot.getStack());
                    }

                }
            }
        });
        // Code to add external slots
        for (Direction dir : Direction.values()) {
            BlockContext context = BlockContext.create(this.getWorld(), this.craftingStationEntity.getPos());
            Optional<BlockPos> opt = context.run((BiFunction<World, BlockPos, BlockPos>) (world, blockPos) -> blockPos.offset(dir));
            if (opt.isPresent()) {
                BlockEntity te = playerInventory.player.world.getBlockEntity(opt.get());
                if (te != null && !(te instanceof CraftingStationEntity)) {
                    if (playerInventory.player.world.getBlockState(opt.get()).getBlock() instanceof ChestBlock) {
                        addInventory(ChestBlock.getInventory((ChestBlock) playerInventory.player.world.getBlockState(opt.get()).getBlock(), playerInventory.player.world.getBlockState(opt.get()), playerInventory.player.world, opt.get(), true), mainInterface);
                        break;
                    }
                    if (playerInventory.player.world.getBlockState(opt.get()).getBlock() instanceof InventoryProvider) {
                        addInventory(((InventoryProvider) te).getInventory(playerInventory.player.world.getBlockState(opt.get()), playerInventory.player.world, opt.get()), mainInterface);
                        break;
                    }
                    if (te instanceof Inventory) {
                        addInventory((Inventory) te, mainInterface);
                        break;
                    }
                }
            }
        }
        onContentChanged(resultInventory);
    }

    void addInventory(Inventory inv, WInterface mainInterface) {
        if (inv != null) {
            getInventories().put(2, inv);
            for (int i = 0; i < inv.getInvSize() / 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (inv.isValidInvStack(i, inv.getInvStack(i)))
                    mainInterface.createChild(WSlot::new).setInventoryNumber(2).setSlotNumber((5 * i) + j);
                }
            }
            for (int i = 0; i < inv.getInvSize() % 5; i++) {
                if (inv.isValidInvStack(i, inv.getInvStack(i)))
                mainInterface.createChild(WSlot::new).setInventoryNumber(2).setSlotNumber((inv.getInvSize() / 5) * 5 + i);
            }
        }
    }

    protected void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        if (!world.isClient) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
            if (optional.isPresent()) {
                CraftingRecipe craftingRecipe = optional.get();
                if (resultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe)) {
                    itemStack = craftingRecipe.craft(craftingInventory);
                }
            }
            resultInventory.setInvStack(0, itemStack);
            super.onContentChanged(resultInventory);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkRegistry.SLOT_UPDATE_PACKET, NetworkRegistry.createSlotUpdatePacket(syncId, 0, 3, itemStack));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        BlockContext context = BlockContext.create(this.getWorld(), this.craftingStationEntity.getPos());
        context.run((world, blockPos) -> {
            updateResult(this.syncId, world, this.getPlayerInventory().player, craftInv, resultInventory);
        });
    }
}

package com.ashindigo.craftingstation;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spinnery.common.container.BaseContainer;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.common.utility.StackUtilities;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;
import spinnery.widget.api.Action;

import java.util.Optional;
import java.util.function.BiFunction;

public class CraftingStationContainer extends BaseScreenHandler { // Mess of a class, just run away
    public static final int INVENTORY = 1;
    public static final int ATTACHED_INVENTORY = 2;
    public static final int RESULT_INVENTORY = 3;

    public CraftingRecipe cachedRecipe = null;

    CraftingStationEntity craftingStationEntity;
    CraftingResultInventory resultInventory;
    CraftingStationInventory craftingInventory;

    public CraftingStationContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
        super(synchronizationID, playerInventory);
        craftingStationEntity = ((CraftingStationEntity) getWorld().getBlockEntity(pos));

        WInterface mainInterface = getInterface();

        craftingInventory = new CraftingStationInventory(this, craftingStationEntity.inventory);
        craftingStationEntity.inventory.addListener(this::onContentChanged);

        WSlot.addHeadlessPlayerInventory(mainInterface);

        // Add the base slots for crafting
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                mainInterface.createChild(WSlot::new).setSlotNumber(x + y * 3).setInventoryNumber(INVENTORY);
            }
        }

        // If the result slot's inventory non existant than make it
        if (resultInventory == null) {
            resultInventory = new CraftingResultInventory() {
                @Override
                public void setStack(int slot, ItemStack stack) {
                    super.setStack(slot, stack);

                    if (!world.isClient) {
                        if (!craftingStationEntity.inventory.isEmpty() && world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world).isPresent() && !stack.isItemEqual(getStack(0))) {
                            onContentChanged(null);
                        }
                    }
                    if (craftingStationEntity.inventory.isEmpty()) {
                        super.setStack(0, ItemStack.EMPTY);
                    }
                }
            };
        }

        super.addInventory(INVENTORY, craftingStationEntity.inventory);
        super.addInventory(RESULT_INVENTORY, resultInventory);

        WResultSlot resultSlot = mainInterface.createChild(WResultSlot::new).setSlotNumber(0).setInventoryNumber(3);

        // No insertion, only take
        resultSlot.accept(ItemStack.EMPTY.getItem());
        resultSlot.setWhitelist();

        // Trigger the crafting using a vanilla inv slot.
        resultSlot.addConsumer((action, subtype) -> {
            if (action == Action.PICKUP || action == Action.PICKUP_ALL || action == Action.QUICK_MOVE) {
                if (subtype == Action.Subtype.FROM_CURSOR_TO_SLOT_DEFAULT_FULL_STACK || subtype == Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM || subtype == Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK || subtype == Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK || subtype == Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK) {
                    if (!resultSlot.getStack().isEmpty()) {
                        if (!world.isClient) {
                            new CraftingResultSlot(playerInventory.player, craftingInventory, resultInventory, 0, 0, 0).onTakeItem(playerInventory.player, resultSlot.getStack());
                        }
                    }
                }
            }
        });

        // Code to add external slots
        for (Direction direction : Direction.values()) {
            ScreenHandlerContext context = ScreenHandlerContext.create(this.getWorld(), this.craftingStationEntity.getPos());
            Optional<BlockPos> optional = context.run((BiFunction<World, BlockPos, BlockPos>) (world, blockPos) -> blockPos.offset(direction));
            if (optional.isPresent()) {
                BlockEntity blockEntity = playerInventory.player.world.getBlockEntity(optional.get());
                if (blockEntity != null && !(blockEntity instanceof CraftingStationEntity)) {
                    if (playerInventory.player.world.getBlockState(optional.get()).getBlock() instanceof ChestBlock) {
                        addAttachedInventory(ChestBlock.getInventory((ChestBlock) playerInventory.player.world.getBlockState(optional.get()).getBlock(), playerInventory.player.world.getBlockState(optional.get()), playerInventory.player.world, optional.get(), true), mainInterface);
                        break;
                    }
                    if (playerInventory.player.world.getBlockState(optional.get()).getBlock() instanceof InventoryProvider) {
                        addAttachedInventory(((InventoryProvider) blockEntity).getInventory(playerInventory.player.world.getBlockState(optional.get()), playerInventory.player.world, optional.get()), mainInterface);
                        break;
                    }
                    if (blockEntity instanceof Inventory) {
                        addAttachedInventory((Inventory) blockEntity, mainInterface);
                        break;
                    }
                }
            }
        }
        onContentChanged(resultInventory);
    }

    void addAttachedInventory(Inventory inventory, WInterface mainInterface) {
        if (inventory != null) {
            addInventory(ATTACHED_INVENTORY, inventory);
            for (int i = 0; i < inventory.size() / 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (inventory.isValid(i, inventory.getStack(i)))
                    mainInterface.createChild(WSlot::new).setInventoryNumber(2).setSlotNumber((5 * i) + j);
                }
            }
            for (int i = 0; i < inventory.size() % 5; i++) {
                if (inventory.isValid(i, inventory.getStack(i)))
                mainInterface.createChild(WSlot::new).setInventoryNumber(2).setSlotNumber((inventory.size() / 5) * 5 + i);
            }
        }
    }

    protected void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
        ItemStack itemStack = ItemStack.EMPTY;

        if (cachedRecipe != null && cachedRecipe.matches(craftingInventory, world)) {
            itemStack = cachedRecipe.craft(craftingInventory);
        } else {
            Optional<CraftingRecipe> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
            if (optional.isPresent()) {
                CraftingRecipe craftingRecipe = optional.get();
                if (craftingRecipe.matches(craftingInventory, world)) {
                    itemStack = craftingRecipe.craft(craftingInventory);
                }
                cachedRecipe = craftingRecipe;
            } else {
                cachedRecipe = null;
            }
        }

        resultInventory.setStack(0, itemStack);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ScreenHandlerContext context = ScreenHandlerContext.create(this.getWorld(), this.craftingStationEntity.getPos());
        context.run((world, blockPos) -> {
            updateResult(this.syncId, world, this.getPlayerInventory().player, craftingInventory, resultInventory);
        });
    }

    @Override
    public void onSlotAction(int slotNumber, int inventoryNumber, int button, Action action, PlayerEntity player) {
        WSlot slotT = null;

        for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
            if (widget instanceof WSlot && ((WSlot) widget).getSlotNumber() == slotNumber && ((WSlot) widget).getInventoryNumber() == inventoryNumber) {
                slotT = (WSlot) widget;
            }
        }

        if (slotT == null || slotT.isLocked()) {
            return;
        }

        WSlot slotA = slotT;

        ItemStack stackA = slotA.getStack().copy();
        ItemStack stackB = player.inventory.getCursorStack().copy();

        PlayerInventory inventory = getPlayerInventory();

        switch (action) {
            case PICKUP: {

                if (!StackUtilities.equalItemAndTag(stackA, stackB)) {
                    if (button == 0) { // Interact with existing // LMB
                        if (slotA.isOverrideMaximumCount()) {
                            if (stackA.isEmpty()) {
                                if (slotA.refuses(stackB)) return;

                                slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK);
                                StackUtilities.merge(stackB, stackA, stackB.getMaxCount(), slotA.getMaxCount()).apply(inventory::setCursorStack, slotA::acceptStack);
                            } else if (stackB.isEmpty()) {
                                if (slotA.refuses(stackB)) return;

                                slotA.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK);
                                StackUtilities.merge(stackA, stackB, slotA.getInventoryNumber() == PLAYER_INVENTORY ? stackB.getMaxCount() : slotA.getMaxCount(), stackB.getMaxCount()).apply(slotA::acceptStack, inventory::setCursorStack);
                            }
                        } else {
                            if (!stackB.isEmpty() && slotA.refuses(stackB)) return;

                            slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_DEFAULT_FULL_STACK);

                            if (!StackUtilities.equalItemAndTag(stackA, stackB)) {
                                slotA.setStack(stackB);
                                player.inventory.setCursorStack(stackA);
                            } else {
                                StackUtilities.merge(stackA, stackB, stackA.isEmpty() || slotA.getInventoryNumber() == PLAYER_INVENTORY ? stackB.getMaxCount() : slotA.getMaxCount(), stackB.getMaxCount()).apply(slotA::acceptStack, inventory::setCursorStack);
                            }
                        }
                    } else if (button == 1 && !stackB.isEmpty()) { // Interact with existing // RMB
                        slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM);
                        StackUtilities.merge(inventory::getCursorStack, slotA::getStack, inventory.getCursorStack()::getMaxCount, () -> (slotA.getStack().getCount() == slotA.getMaxCount() ? 0 : slotA.getStack().getCount() + 1)).apply(inventory::setCursorStack, slotA::setStack);
                    } else if (button == 1) { // Split existing // RMB
                        slotA.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_DEFAULT_HALF_STACK);
                        StackUtilities.merge(slotA::getStack, inventory::getCursorStack, inventory.getCursorStack()::getMaxCount, () -> Math.max(1, Math.min(slotA.getStack().getMaxCount() / 2, slotA.getStack().getCount() / 2))).apply(slotA::setStack, inventory::setCursorStack);
                    }
                } else {
                    if (button == 0) {
                        if (slotA instanceof WResultSlot) {
                            if (stackB.getCount()  + slotA.getStack().getCount() > stackB.getMaxCount()) {
                                return;
                            }

                            slotA.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK);
                            StackUtilities.merge(stackA, stackB, slotA.getInventoryNumber() == PLAYER_INVENTORY ? stackB.getMaxCount() : slotA.getMaxCount(), stackB.getMaxCount()).apply(slotA::acceptStack, inventory::setCursorStack);
                        } else {
                            if (slotA.refuses(stackB)) return;

                            slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK);
                            StackUtilities.merge(inventory::getCursorStack, slotA::getStack, stackB::getMaxCount, slotA::getMaxCount).apply(inventory::setCursorStack, slotA::setStack); // Add to existing // LMB
                        }

                    } else {
                        if (slotA.refuses(stackB)) return;

                        slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM);
                        StackUtilities.merge(inventory::getCursorStack, slotA::getStack, inventory.getCursorStack()::getMaxCount, () -> (slotA.getStack().getCount() == slotA.getMaxCount() ? 0 : slotA.getStack().getCount() + 1)).apply(inventory::setCursorStack, slotA::setStack); // Add to existing // RMB
                    }
                }
                break;
            }
            case CLONE: {
                if (player.isCreative()) {
                    stackB = new ItemStack(stackA.getItem(), stackA.getMaxCount()); // Clone existing // MMB
                    stackB.setTag(stackA.getTag());
                    inventory.setCursorStack(stackB);
                }
                break;
            }
            case QUICK_MOVE: {
                WSlot targetSlot = null;
                for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
                    if (widget instanceof WSlot && ((WSlot) widget).getLinkedInventory() != slotA.getLinkedInventory()) {
                        WSlot slotB = ((WSlot) widget);
                        ItemStack stackC = slotB.getStack();
                        stackA = slotA.getStack();

                        if ((!slotA.getStack().isEmpty() && stackC.isEmpty()) || (StackUtilities.equalItemAndTag(stackA, stackC) && stackC.getCount() < (slotB.getInventoryNumber() == PLAYER_INVENTORY ? stackA.getMaxCount() : slotB.getMaxCount()))) {
                            targetSlot = slotB;
                            break;
                        }
                    }
                }
                if (slotA instanceof WResultSlot && targetSlot != null) {
                    while (!slotA.getStack().isEmpty() && (targetSlot.getStack().getCount() + slotA.getStack().getCount() <= targetSlot.getStack().getMaxCount())) {
                        if (targetSlot.refuses(stackA)) continue;
                        if (targetSlot.isLocked()) continue;

                        ItemStack stackC = targetSlot.getStack();
                        stackA = slotA.getStack();

                        int maxB = stackC.isEmpty() || targetSlot.getInventoryNumber() == PLAYER_INVENTORY ? stackA.getMaxCount() : targetSlot.getMaxCount();
                        StackUtilities.merge(slotA::getStack, targetSlot::getStack, slotA::getMaxCount, () -> maxB).apply(slotA::setStack, targetSlot::setStack);
                        slotA.consume(action, Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK);
                    }
                } else if (targetSlot != null && targetSlot.getLinkedInventory() != slotA.getLinkedInventory()) {
                    ItemStack stackC = targetSlot.getStack();
                    stackA = slotA.getStack();

                    if (targetSlot.refuses(stackA)) return;
                    if (targetSlot.isLocked()) return;

                    if ((!slotA.getStack().isEmpty() && stackC.isEmpty()) || (StackUtilities.equalItemAndTag(stackA, stackC) && stackC.getCount() < (targetSlot.getInventoryNumber() == PLAYER_INVENTORY ? stackA.getMaxCount() : targetSlot.getMaxCount()))) {
                        int maxB = stackC.isEmpty() || targetSlot.getInventoryNumber() == PLAYER_INVENTORY ? stackA.getMaxCount() : targetSlot.getMaxCount();
                        slotA.consume(action, Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK);
                        StackUtilities.merge(slotA::getStack, targetSlot::getStack, slotA::getMaxCount, () -> maxB).apply(slotA::setStack, ((WSlot) targetSlot)::setStack);
                        break;
                    }
                }

                break;
            }
            case PICKUP_ALL: {
                if (slotA instanceof WResultSlot) {
                    return;
                }

                for (WAbstractWidget widget : getInterface().getAllWidgets()) {
                    if (widget instanceof WSlot && StackUtilities.equalItemAndTag(((WSlot) widget).getStack(), stackB)) {
                        WSlot slotB = (WSlot) widget;

                        if (slotB instanceof WResultSlot) {
                            return;
                        }

                        if (slotB.isLocked()) continue;

                        slotB.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK);
                        StackUtilities.merge(slotB::getStack, inventory::getCursorStack, slotB::getMaxCount, stackB::getMaxCount).apply(slotB::setStack, inventory::setCursorStack);
                    }
                }
            }
        }
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return CraftingStation.craftingStationScreenHandler;
    }
}

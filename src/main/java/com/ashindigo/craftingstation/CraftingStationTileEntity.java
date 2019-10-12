package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.gui.CraftingStationGui;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CraftingStationTileEntity extends BlockEntity implements NameableContainerProvider, InventoryProvider {
    private SidedInventory inventory;

    public CraftingStationTileEntity() {
        super(CraftingStation.type);
        inventory = new CraftingStationInventory(null,3, 3);
    }

    @Override
    public Container createMenu(int var1, PlayerInventory var2, PlayerEntity var3) {
        CraftingStationGui craftingStationGui = new CraftingStationGui(var1, var2, BlockContext.create(var3.world, this.pos));
        return craftingStationGui;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.craftingstation.craftingstation");
    }

    @Override
    public void fromTag(CompoundTag compound) {
        super.fromTag(compound);
        if (compound.containsKey("inv")) {
            ListTag listTag = compound.getList("inv", 10);
            for (int i = 0; i < listTag.size(); i++) {
                inventory.setInvStack(i, ItemStack.fromTag(listTag.getCompoundTag(i)));
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < inventory.getInvSize(); i++) {
            listTag.add(i, inventory.getInvStack(i).toTag(new CompoundTag()));
        }
        compound.put("inv", listTag);
        return super.toTag(compound);
    }

    @Override
    public SidedInventory getInventory(BlockState var1, IWorld var2, BlockPos var3) {
        return inventory;
    }
}

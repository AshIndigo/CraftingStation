package com.ashindigo.craftingstation;

import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CraftingStationTileEntity extends BlockEntity implements NameableContainerProvider, InventoryProvider {
    public CraftingStationTileEntity() {
        super(CraftingStation.type);
    }

    @Override
    public Container createMenu(int var1, PlayerInventory var2, PlayerEntity var3) {
        return new CraftingStationContainer(var1);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.craftingstation.craftingstation");
    }

    @Override
    public SidedInventory getInventory(BlockState var1, IWorld var2, BlockPos var3) {
        return null;
    }
}

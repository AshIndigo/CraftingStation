package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.CraftingStation;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import spinnery.common.BaseInventory;
import spinnery.util.InventoryUtilities;

public class CraftingStationEntity extends BlockEntity implements BlockEntityClientSerializable {

    public BaseInventory inventory;

    public CraftingStationEntity() {
        super(CraftingStation.craftingStationEntity);
        inventory = new BaseInventory(9);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory = InventoryUtilities.read(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        InventoryUtilities.write(inventory, tag);
        super.toTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        super.fromTag(tag);
        inventory = InventoryUtilities.read(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        InventoryUtilities.write(inventory, tag);
        super.toTag(tag);
        return tag;
    }
}
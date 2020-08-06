package com.ashindigo.craftingstation.entity;

import com.ashindigo.craftingstation.CraftingStation;
import com.ashindigo.craftingstation.handler.CraftingStationHandler;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;

public class CraftingStationEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {

    public BaseInventory inventory;

    public CraftingStationEntity() {
        super(CraftingStation.craftingStationEntity);
        inventory = new BaseInventory(9);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
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
        super.fromTag(getCachedState(), tag);
        inventory = InventoryUtilities.read(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        InventoryUtilities.write(inventory, tag);
        super.toTag(tag);
        return tag;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(CraftingStation.craftingStationBlock.getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CraftingStationHandler(syncId, inv, pos);
    }
}
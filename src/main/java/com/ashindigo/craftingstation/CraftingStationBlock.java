package com.ashindigo.craftingstation;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;

public class CraftingStationBlock extends BlockWithEntity {

    public CraftingStationBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CraftingStationEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(CraftingStation.ID, player, (buffer) -> { buffer.writeBlockPos(pos); buffer.writeInt(0); buffer.writeInt(0); buffer.writeInt(0); buffer.writeText(new TranslatableText(this.getTranslationKey())); });
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            CraftingStationEntity inventory = ((CraftingStationEntity) Objects.requireNonNull(world.getBlockEntity(pos)));
            for (int i = 0; i < inventory.inventory.getInvSize(); i++) {
                world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), inventory.inventory.getInvStack(i)));
            }
            world.updateNeighbors(pos, this);
           super.onBlockRemoved(state, world, pos, newState, moved);
        }

    }

    @Override
    @Deprecated
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }
}
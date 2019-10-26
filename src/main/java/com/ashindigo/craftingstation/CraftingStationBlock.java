package com.ashindigo.craftingstation;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

@SuppressWarnings("WeakerAccess")
public class CraftingStationBlock extends BlockWithEntity implements InventoryProvider {

    public CraftingStationBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new CraftingStationTileEntity();
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (world.isClient) {
            return true;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CraftingStationTileEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(CraftingStation.craftingStationID, player, (buf) -> buf.writeBlockPos(pos));
        }

        return true;
    }

    @Override
    public void onBlockRemoved(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1) {
        if (blockState_1.getBlock() != blockState_2.getBlock()) {
            ItemScatterer.spawn(world_1, blockPos_1, getInventory(blockState_2, world_1, blockPos_1));
            world_1.updateHorizontalAdjacent(blockPos_1, this);
            super.onBlockRemoved(blockState_1, world_1, blockPos_1, blockState_2, boolean_1);
        }
    }

    @Override
    public SidedInventory getInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
        return ((InventoryProvider) iWorld.getBlockEntity(blockPos)).getInventory(blockState, iWorld, blockPos);
    }

    @Override
    @Deprecated
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaque(BlockState blockState_1) {
        return false;
    }

    @Override
    public boolean isSimpleFullBlock(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return false;
    }


}

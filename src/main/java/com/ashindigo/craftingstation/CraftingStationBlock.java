package com.ashindigo.craftingstation;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CraftingStationBlock extends BlockWithEntity implements InventoryProvider {

    protected CraftingStationBlock(Settings block$Settings_1) {
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
            ContainerProviderRegistry.INSTANCE.openContainer(CraftingStation.craftingStationID, player, (buf) -> {
                buf.writeBlockPos(pos);
            });
        }

        return true;
    }

    @Override
    public SidedInventory getInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
        return null;
    }
}

package com.ashindigo.craftingstation;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeBlockPos(pos);
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText(getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new CraftingStationContainer(syncId, inv, pos);
                }
            });

        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getWorld().getBlockEntity(pos);
        if (blockEntity instanceof CraftingStationEntity) {
            ItemScatterer.spawn(world.getWorld(), pos, ((CraftingStationEntity) blockEntity).inventory);
            world.getWorld().updateComparators(pos, this);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    @Deprecated
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }
}
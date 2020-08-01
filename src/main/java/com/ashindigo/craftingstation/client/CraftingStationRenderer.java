package com.ashindigo.craftingstation.client;

import com.ashindigo.craftingstation.entity.CraftingStationEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class CraftingStationRenderer extends BlockEntityRenderer<CraftingStationEntity> {

    public CraftingStationRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CraftingStationEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }
}

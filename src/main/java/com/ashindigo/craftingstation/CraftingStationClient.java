package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.client.CraftingStationRenderer;
import com.ashindigo.craftingstation.screen.CraftingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;

public class CraftingStationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(CraftingStation.craftingStationScreenHandler, CraftingStationScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(CraftingStation.craftingStationBlock, RenderLayer.getCutout());
        BlockEntityRendererRegistry.INSTANCE.register(CraftingStation.craftingStationEntity, CraftingStationRenderer::new);
    }
}

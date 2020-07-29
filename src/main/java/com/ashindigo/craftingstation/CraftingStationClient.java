package com.ashindigo.craftingstation;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;

public class CraftingStationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(CraftingStation.craftingStationScreenHandler, CraftingStationScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(CraftingStation.craftingStationBlock, RenderLayer.getCutout());
    }
}

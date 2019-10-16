package com.ashindigo.craftingstation;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.ContainerProvider;

public class CraftingStationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(CraftingStation.craftingStationID, (syncId, identifier, player, buf) -> new CraftingStationScreen((CraftingStationContainer) ((ContainerProvider)player.world.getBlockEntity(buf.readBlockPos())).createMenu(syncId, player.inventory, player), player.inventory));
    }
}

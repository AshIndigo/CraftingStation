package com.ashindigo.craftingstation;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.ContainerProvider;

@SuppressWarnings("unused")
public class CraftingStationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(CraftingStation.craftingStationID, (syncId, identifier, player, buf) -> new CottonScreen<>((CraftingStationContainer) ((ContainerProvider) player.world.getBlockEntity(buf.readBlockPos())).createMenu(syncId, player.inventory, player), player));
    }
}

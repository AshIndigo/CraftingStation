package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.gui.CraftingStationGui;
import com.ashindigo.craftingstation.gui.CraftingStationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;

public class CraftingStationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(CraftingStation.craftingStationID, (syncId, identifier, player, buf) -> new CraftingStationScreen(new CraftingStationGui(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())), player));
    }
}

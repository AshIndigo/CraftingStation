package com.ashindigo.craftingstation;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class CraftingStationClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(CraftingStation.ID,
                (id, identifier, player, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    int x = buf.readInt();
                    int y = buf.readInt();
                    int m = buf.readInt();
                    Text text = buf.readText();
                    return new CraftingStationScreen(text, new CraftingStationContainer(id, player.inventory, pos, x, y, m), player, x, y);
                });
    }
}

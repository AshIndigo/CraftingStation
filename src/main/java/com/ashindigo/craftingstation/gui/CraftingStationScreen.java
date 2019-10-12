package com.ashindigo.craftingstation.gui;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;

public class CraftingStationScreen extends CottonScreen<CraftingStationGui> {

    public CraftingStationScreen(CraftingStationGui container, PlayerEntity player) {
        super(container, player);
    }
}

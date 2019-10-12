package com.ashindigo.craftingstation;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;

public class CraftingStationContainer extends Container {

    protected CraftingStationContainer(int int_1) {
        super(null, int_1);
    }

    @Override
    public boolean canUse(PlayerEntity var1) {
        return false;
    }
}

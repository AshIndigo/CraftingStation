package com.ashindigo.craftingstation;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class CraftingStation implements ModInitializer {

    public static final String MODID = "craftingstation";
    public static BlockEntityType<?> type;
    public static ContainerFactory<Container> stationContainer;
    public static final Identifier craftingStationID = new Identifier(MODID, MODID);

    @Override
    public void onInitialize() {
        Block craftingStationBlock = new CraftingStationBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F, 2.5F).build());
        Registry.register(Registry.BLOCK, craftingStationID, craftingStationBlock);
        Registry.register(Registry.ITEM, craftingStationID, new BlockItem(craftingStationBlock, new Item.Settings().maxCount(64).group(ItemGroup.MISC)));
        type = BlockEntityType.Builder.create(CraftingStationTileEntity::new, craftingStationBlock).build(null);
        Registry.register(Registry.BLOCK_ENTITY, craftingStationID, type);
        stationContainer = (syncId, identifier, player, buf) -> ((ContainerProvider) Objects.requireNonNull(player.world.getBlockEntity(buf.readBlockPos()))).createMenu(syncId, player.inventory, player);
        ContainerProviderRegistry.INSTANCE.registerFactory(craftingStationID, stationContainer);
    }
}

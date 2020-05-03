package com.ashindigo.craftingstation;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CraftingStation implements ModInitializer {

    public static final String MODID = "craftingstation";
    public static final Identifier ID = new Identifier(MODID, MODID);
    public static BlockEntityType<CraftingStationEntity> craftingStationEntity;

    @Override
    public void onInitialize() {
        CraftingStationBlock craftingStationBlock = new CraftingStationBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F, 2.5F).sounds(BlockSoundGroup.WOOD).nonOpaque().build());
        Registry.register(Registry.BLOCK, ID, craftingStationBlock);
        Registry.register(Registry.ITEM, ID, new BlockItem(craftingStationBlock, new Item.Settings().maxCount(64).group(ItemGroup.MISC)));
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, MODID), (syncId, id, player, buffer) -> new CraftingStationContainer(syncId, player.inventory, buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
        craftingStationEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, ID, BlockEntityType.Builder.create(CraftingStationEntity::new, craftingStationBlock).build(null));
        BlockRenderLayerMap.INSTANCE.putBlock(craftingStationBlock, RenderLayer.getCutout());
    }
}

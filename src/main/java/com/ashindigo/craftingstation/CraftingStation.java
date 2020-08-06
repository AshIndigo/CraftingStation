package com.ashindigo.craftingstation;

import com.ashindigo.craftingstation.block.CraftingStationBlock;
import com.ashindigo.craftingstation.compat.CraftingStationContainerInfo;
import com.ashindigo.craftingstation.entity.CraftingStationEntity;
import com.ashindigo.craftingstation.handler.CraftingStationHandler;
import me.shedaniel.rei.server.ContainerInfoHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
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
    public static CraftingStationBlock craftingStationBlock;
    public static ExtendedScreenHandlerType<CraftingStationHandler> craftingStationScreenHandler;

    @Override
    public void onInitialize() {
        craftingStationBlock = new CraftingStationBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F, 2.5F).sounds(BlockSoundGroup.WOOD).nonOpaque());
        Registry.register(Registry.BLOCK, ID, craftingStationBlock);
        Registry.register(Registry.ITEM, ID, new BlockItem(craftingStationBlock, new Item.Settings().maxCount(64).group(ItemGroup.MISC)));
        craftingStationScreenHandler = (ExtendedScreenHandlerType<CraftingStationHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, MODID), (syncId, inventory, buf) -> new CraftingStationHandler(syncId, inventory, buf.readBlockPos()));
        craftingStationEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, ID, BlockEntityType.Builder.create(CraftingStationEntity::new, craftingStationBlock).build(null));
        if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            ContainerInfoHandler.registerContainerInfo(new Identifier("minecraft", "plugins/crafting"), new CraftingStationContainerInfo());
        }
    }
}

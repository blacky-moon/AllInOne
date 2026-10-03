package dev.kineticcat.complexhex;

import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.common.lib.hex.HexArithmetics;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.kineticcat.complexhex.api.casting.iota.ComplexHexIotaTypes;
import dev.kineticcat.complexhex.block.ComplexHexBlocks;
import dev.kineticcat.complexhex.block.entity.ComplexHexBlockEntities;
import dev.kineticcat.complexhex.casting.ComplexHexSpecialHandlers;
import dev.kineticcat.complexhex.casting.ComplexhexPatternRegistry;
import dev.kineticcat.complexhex.casting.arithmetic.ComplexHexArithmetic;
import dev.kineticcat.complexhex.entity.ComplexHexEntities;
import dev.kineticcat.complexhex.item.ComplexHexCreativeTabs;
import dev.kineticcat.complexhex.item.ComplexHexItems;
import dev.kineticcat.complexhex.util.ComplexHexSounds;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Complexhex.MOD_ID)
public class ComplexhexNeoForge {

    public ComplexhexNeoForge(IEventBus modBus, ModContainer modContainer) {
        
        
        
        
        
        
        
        modBus.addListener(this::registerVanillaRegistryContent);
        modBus.addListener(this::buildCreativeTabContents);

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
    }

    private void registerVanillaRegistryContent(RegisterEvent event) {
        if (event.getRegistryKey().equals(HexIotaTypes.REGISTRY.key())) {
            ComplexHexIotaTypes.init();
        } else if (event.getRegistryKey().equals(HexActions.REGISTRY.key())) {
            ComplexhexPatternRegistry.init();
        } else if (event.getRegistryKey().equals(HexArithmetics.REGISTRY.key())) {
            ComplexHexArithmetic.init();
        } else if (event.getRegistryKey().equals(IXplatAbstractions.INSTANCE.getSpecialHandlerRegistry().key())) {
            ComplexHexSpecialHandlers.init();
        } else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            
            
            
            ComplexHexCreativeTabs.init();
        } else if (event.getRegistryKey().equals(Registries.BLOCK)) {
            
            
            
            
            
            
            
            
            
            
            ComplexHexBlocks.init();
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            
            
            
            ComplexHexItems.init();
        } else if (event.getRegistryKey().equals(Registries.ENTITY_TYPE)) {
            ComplexHexEntities.init();
        } else if (event.getRegistryKey().equals(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS)) {
            
            
            
            ComplexHexEntities.initSerializers();
        } else if (event.getRegistryKey().equals(Registries.BLOCK_ENTITY_TYPE)) {
            ComplexHexBlockEntities.init();
        } else if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) {
            ComplexHexSounds.init();
        }
    }

    private void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        ComplexHexItems.registerItemCreativeTab(event, event.getTab());
        ComplexHexBlocks.registerBlockCreativeTab(event::accept, event.getTab());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Complexhex.init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ComplexhexClient.init();
    }
}
package miyucomics.hexical

import miyucomics.hexical.compat.HexicalCompat
import miyucomics.hexical.features.amber_seal.AmberSealItemRenderer
import miyucomics.hexical.features.gauntlet_staff.GauntletStaffCurioRenderers
import miyucomics.hexical.features.media_jar.MediaJarItemRenderer
import miyucomics.hexical.features.patchouli.BlockPage
import miyucomics.hexical.features.patchouli.DyeingBlockPage
import miyucomics.hexical.features.patchouli.DyeingItemPage
import miyucomics.hexical.inits.*
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.common.NeoForge
import vazkii.patchouli.client.book.ClientBookRegistry

@Mod(value = HexicalMain.MOD_ID, dist = [Dist.CLIENT])
class HexicalClient(modBus: IEventBus) {

    init {
        HexicalParticles.init(modBus)
        modBus.addListener(::onClientSetup)
        modBus.addListener(::registerRenderers)
        modBus.addListener(::registerClientExtensions)
        modBus.addListener(::registerParticleProviders)

        modBus.addListener(HexicalKeybinds::registerKeyMappings)

        NeoForge.EVENT_BUS.addListener<ClientTickEvent.Post>(::onClientTick)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            HexicalHooksClient.init()
            HexicalBlocksClient.initScryingOverlays()
            ClientBookRegistry.INSTANCE.pageTypes[HexicalMain.id("block")] = BlockPage::class.java
            ClientBookRegistry.INSTANCE.pageTypes[HexicalMain.id("dyeing_block")] = DyeingBlockPage::class.java
            ClientBookRegistry.INSTANCE.pageTypes[HexicalMain.id("dyeing_item")] = DyeingItemPage::class.java
            if (HexicalCompat.curiosLoaded) {
                GauntletStaffCurioRenderers.register()
            }
        }
    }

    private fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        HexicalBlocksClient.registerRenderers(event)
        HexicalEntitiesClient.registerRenderers(event)
    }

    private val amberSealExtensions = object : IClientItemExtensions {
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = AmberSealItemRenderer
    }

    private val mediaJarExtensions = object : IClientItemExtensions {
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = MediaJarItemRenderer
    }

    private fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerItem(amberSealExtensions, HexicalItems.AMBER_SEAL_ITEM.get())
        event.registerItem(mediaJarExtensions, HexicalItems.MEDIA_JAR_ITEM.get())
    }

    private fun registerParticleProviders(event: RegisterParticleProvidersEvent) {
        HexicalParticlesClient.registerProviders(event)
    }

    private fun onClientTick(event: ClientTickEvent.Post) {
        ClientStorage.ticks += 1
    }
}
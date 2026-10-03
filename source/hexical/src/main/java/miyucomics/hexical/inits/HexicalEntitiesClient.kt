package miyucomics.hexical.inits

import miyucomics.hexical.features.magic_missile.MagicMissileRenderer
import miyucomics.hexical.features.specklikes.MeshRenderer
import miyucomics.hexical.features.specklikes.SpeckRenderer
import miyucomics.hexical.features.specklikes.StrandRenderer
import miyucomics.hexical.features.spike.SpikeRenderer
import net.neoforged.neoforge.client.event.EntityRenderersEvent

object HexicalEntitiesClient {
    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(HexicalEntities.MAGIC_MISSILE_ENTITY.get(), ::MagicMissileRenderer)
        event.registerEntityRenderer(HexicalEntities.SPIKE_ENTITY.get(), ::SpikeRenderer)
        event.registerEntityRenderer(HexicalEntities.SPECK_ENTITY.get(), ::SpeckRenderer)
        event.registerEntityRenderer(HexicalEntities.STRAND_ENTITY.get(), ::StrandRenderer)
        event.registerEntityRenderer(HexicalEntities.MESH_ENTITY.get(), ::MeshRenderer)
    }
}
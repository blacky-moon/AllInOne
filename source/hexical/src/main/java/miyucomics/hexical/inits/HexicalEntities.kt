package miyucomics.hexical.inits

import com.google.common.eventbus.EventBus
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.magic_missile.MagicMissileEntity
import miyucomics.hexical.features.specklikes.mesh.MeshEntity
import miyucomics.hexical.features.specklikes.speck.SpeckEntity
import miyucomics.hexical.features.specklikes.strand.StrandEntity
import miyucomics.hexical.features.spike.SpikeEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object HexicalEntities {
    val ENTITY_TYPES: DeferredRegister<EntityType<*>> = DeferredRegister.create(
        Registries.ENTITY_TYPE,
        HexicalMain.MOD_ID
    )

    val MAGIC_MISSILE_ENTITY = ENTITY_TYPES.register("magic_missile") { _ ->
        EntityType.Builder.of(::MagicMissileEntity, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build("magic_missile")
    }

    val SPIKE_ENTITY = ENTITY_TYPES.register("spike") { _ ->
        EntityType.Builder.of(::SpikeEntity, MobCategory.MISC)
            .sized(1f, 1f)
            .clientTrackingRange(10)
            .updateInterval(1)
            .build("spike")
    }

    val SPECK_ENTITY = ENTITY_TYPES.register("speck") { _ ->
        EntityType.Builder.of(::SpeckEntity, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(32)
            .updateInterval(1)
            .build("speck")
    }

    val STRAND_ENTITY = ENTITY_TYPES.register("strand") { _ ->
        EntityType.Builder.of(::StrandEntity, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(32)
            .updateInterval(1)
            .build("strand")
    }

    val MESH_ENTITY = ENTITY_TYPES.register("mesh") { _ ->
        EntityType.Builder.of(::MeshEntity, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(32)
            .updateInterval(1)
            .build("mesh")
    }

    fun init(modeventBus: IEventBus) {

        ENTITY_TYPES.register(modeventBus)
    }
}
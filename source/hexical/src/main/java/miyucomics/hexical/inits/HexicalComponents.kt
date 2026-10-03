package miyucomics.hexical.inits

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import com.mojang.serialization.Codec
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.amber_seal.AmberSealContents
import miyucomics.hexical.features.mute.MutedComponent
import miyucomics.hexical.features.specklikes.mesh.MeshChronicler
import miyucomics.hexical.features.specklikes.speck.SpeckChronicler
import miyucomics.hexical.features.specklikes.strand.StrandChronicler
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.EntityCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object HexicalComponents {
    // === Data Components для предметов ===
    private val DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, HexicalMain.MOD_ID)

    val MEDIA_HOLDER: DeferredHolder<DataComponentType<*>, DataComponentType<Long>> =
        DATA_COMPONENTS.registerComponentType("media_holder") { builder ->
            builder.persistent(Codec.LONG)
                .networkSynchronized(ByteBufCodecs.VAR_LONG)
        }

    @JvmField
    val LAMP_ACTIVE: DeferredHolder<DataComponentType<*>, DataComponentType<Boolean>> =
        DATA_COMPONENTS.registerComponentType("lamp_active") { builder ->
            builder.persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
        }

    @JvmField
    val HEXTITO_HEX: DeferredHolder<DataComponentType<*>, DataComponentType<CompoundTag>> =
        DATA_COMPONENTS.registerComponentType("hextito_hex") { builder ->
            builder.persistent(CompoundTag.CODEC)
                .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
        }
    val IOTA_HOLDER: DeferredHolder<DataComponentType<*>, DataComponentType<CompoundTag>> =
        DATA_COMPONENTS.registerComponentType("iota_holder") { builder ->
            builder.persistent(CompoundTag.CODEC)
                .networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
        }

    @JvmField
    val SPECK_CHRONICLER_CAP: EntityCapability<ADIotaHolder, Void> = EntityCapability.create(
        HexicalMain.id("speck_chronicler"),
        ADIotaHolder::class.java,
        Void::class.java
    )

    @JvmField
    val STRAND_CHRONICLER_CAP: EntityCapability<ADIotaHolder, Void> = EntityCapability.create(
        HexicalMain.id("strand_chronicler"),
        ADIotaHolder::class.java,
        Void::class.java
    )

    @JvmField
    val AMBER_SEAL_CONTENTS: DeferredHolder<DataComponentType<*>, DataComponentType<AmberSealContents>> =
        DATA_COMPONENTS.registerComponentType("amber_seal_contents") { builder ->
            builder.persistent(AmberSealContents.CODEC)
                .networkSynchronized(AmberSealContents.STREAM_CODEC)
        }

    @JvmField
    val MESH_CHRONICLER_CAP: EntityCapability<ADIotaHolder, Void> = EntityCapability.create(
        HexicalMain.id("mesh_chronicler"),
        ADIotaHolder::class.java,
        Void::class.java
    )

    @JvmField
    val MUTED_CAP: EntityCapability<MutedComponent, Void?> =
        EntityCapability.createVoid(
            HexicalMain.id("muted"),
            MutedComponent::class.java
        )

    fun register(bus: IEventBus) {
        DATA_COMPONENTS.register(bus)
        bus.addListener(::registerCapabilities)
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerEntity(
            SPECK_CHRONICLER_CAP,
            HexicalEntities.SPECK_ENTITY.get()
        ) { entity, _ -> SpeckChronicler(entity) }

        event.registerEntity(
            STRAND_CHRONICLER_CAP,
            HexicalEntities.STRAND_ENTITY.get()
        ) { entity, _ -> StrandChronicler(entity) }

        event.registerEntity(
            MESH_CHRONICLER_CAP,
            HexicalEntities.MESH_ENTITY.get()
        ) { entity, _ -> MeshChronicler(entity) }
    }
}
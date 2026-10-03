package miyucomics.hexical.features.amber_seal

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional

data class AmberSealContents(
    val blockState: BlockState,
    val blockEntityTag: CompoundTag?
) {
    companion object {
        @JvmField
        val CODEC: Codec<AmberSealContents> = RecordCodecBuilder.create { instance ->
            instance.group(
                BlockState.CODEC.fieldOf("block_state").forGetter { it.blockState },
                CompoundTag.CODEC.optionalFieldOf("block_entity_tag").forGetter {
                    Optional.ofNullable(it.blockEntityTag)
                }
            ).apply(instance) { state, beTag ->
                AmberSealContents(state, beTag.orElse(null))
            }
        }

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AmberSealContents> =
            StreamCodec.composite(
                ByteBufCodecs.fromCodec(BlockState.CODEC), { it.blockState },
                // ИСПРАВЛЕНО: используем optional для nullable CompoundTag
                ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG), { Optional.ofNullable(it.blockEntityTag) },
                { state, beTagOpt -> AmberSealContents(state, beTagOpt.orElse(null)) }
            )
    }
}
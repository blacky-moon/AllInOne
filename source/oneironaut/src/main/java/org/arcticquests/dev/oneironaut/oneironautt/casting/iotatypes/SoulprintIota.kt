package org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.phys.Vec3
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaKeyable
import org.arcticquests.dev.oneironaut.oneironautt.item.BottomlessMediaItem
import net.minecraft.core.UUIDUtil

import java.util.UUID

class SoulprintIota(val entityUuid: UUID, val entityName: String) : Iota({ TYPE }), IdeaKeyable {

    override fun isTruthy() = true

    override fun toleratesOther(that: Iota): Boolean {
        if (that.type === this.type.get() && that is SoulprintIota) {
            //not checking the entity name because player name changes are a thing
            return this.entityUuid == that.entityUuid
        }
        return false
    }

    override fun hashCode(): Int {
        return entityUuid.hashCode()
    }

    fun getEntity(): UUID = entityUuid

    override fun display(): Component {
        val original = Component.translatable("hexcasting.iota.oneironaut:uuid.label", entityName)
        val soulglimmerStack = HexItems.UUID_PIGMENT.defaultInstance
        val soulglimmerColor = FrozenPigment(soulglimmerStack, entityUuid)
        val coloredStyle = original.style.withColor(
            IXplatAbstractions.INSTANCE.getColorProvider(soulglimmerColor).getColor(
                BottomlessMediaItem.time.toFloat(),
                Vec3.ZERO
            )
        )
        return original.copy().setStyle(coloredStyle)
    }

    override fun getKey(): String {
        return getEntity().toString() + "soul"
    }

    override fun isValidKey(env: CastingEnvironment): Boolean {
        return true
    }

    companion object {
        @JvmField
        val TYPE: IotaType<SoulprintIota> = object : IotaType<SoulprintIota>() {
            override fun color() = 0xff7a63bc.toInt()

            override fun codec(): MapCodec<SoulprintIota> =
            RecordCodecBuilder.mapCodec { instance ->
                    instance.group(
                            com.mojang.serialization.Codec.STRING.xmap(UUID::fromString, UUID::toString)
                                    .fieldOf("iota_uuid").forGetter(SoulprintIota::entityUuid),
                            com.mojang.serialization.Codec.STRING.fieldOf("entity_name").forGetter(SoulprintIota::entityName)
                    ).apply(instance, ::SoulprintIota)
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SoulprintIota> =
                StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, SoulprintIota::entityUuid,
                    ByteBufCodecs.STRING_UTF8, SoulprintIota::entityName,
                    ::SoulprintIota
                )
        }
    }
}
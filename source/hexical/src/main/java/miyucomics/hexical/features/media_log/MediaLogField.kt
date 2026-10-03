package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import miyucomics.hexpose.utils.RingBuffer
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

class MediaLogPayload(val data: CompoundTag) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<MediaLogPayload>(HexicalMain.id("media_log"))

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MediaLogPayload> = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, { it.data },
            { data -> MediaLogPayload(data) }
        )
    }
}

class MediaLogField : PlayerField {
    var patterns: RingBuffer<HexPattern> = RingBuffer(32)
    var stack: RingBuffer<Component> = RingBuffer(8)
    var mishap: Component = Component.empty()
    var active = true

    fun saveMishap(text: Component) {
        mishap = text
    }

    fun pushPattern(pattern: HexPattern) {
        patterns.add(pattern)
    }

    fun saveStack(iotas: List<Iota>) {
        stack.clear()
        iotas.forEach { iota -> stack.add(iota.display()) }
    }

    override fun readNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        if (!compound.contains("media_log"))
            return
        fromNbt(compound.getCompound("media_log"), registries)
    }

    override fun writeNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        compound.put("media_log", toNbt(registries))
    }

    fun fromNbt(log: CompoundTag, registries: HolderLookup.Provider) {
        log.getList("patterns", Tag.TAG_COMPOUND.toInt()).forEach { patternTag ->
            val patternCompound = patternTag as CompoundTag
            val startDir = patternCompound.getString("startDir")
            val angles = patternCompound.getString("angles")
            val dir = HexDir.entries.find { it.name == startDir } ?: HexDir.EAST
            patterns.add(HexPattern.fromAngles(angles, dir))
        }
        log.getList("stack", Tag.TAG_STRING.toInt()).forEach { iota ->
            val text = Component.Serializer.fromJson(
                (iota as StringTag).asString,
                registries
            )
            text?.let { stack.add(it) }
        }
        this.mishap = Component.Serializer.fromJson(
            log.getString("mishap"),
            registries
        ) ?: Component.empty()
    }

    fun toNbt(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().also { compound ->
            val patternsList = ListTag()
            // ИСПРАВЛЕНО: используем метод buffer() вместо приватного поля buffer
            for (pattern in patterns.buffer()) {
                val patternCompound = CompoundTag()
                patternCompound.putString("startDir", pattern.startDir.name)
                patternCompound.putString("angles", pattern.anglesSignature())
                patternsList.add(patternCompound)
            }
            compound.put("patterns", patternsList)

            val stackList = ListTag()
            for (component in stack.buffer()) {
                stackList.add(
                    StringTag.valueOf(
                        Component.Serializer.toJson(component, registries)
                    )
                )
            }
            compound.put("stack", stackList)
            compound.putString(
                "mishap",
                Component.Serializer.toJson(mishap, registries)
            )
        }
    }

    companion object {
        @JvmStatic
        fun isEnvCompatible(env: CastingEnvironment) = env is StaffCastEnv || env is PackagedItemCastEnv
    }
}

fun Player.getMediaLog() = this.getHexicalPlayerManager().get(MediaLogField::class)

fun ServerPlayer.syncMediaLog() {
    val tag = this.getMediaLog().toNbt(this.registryAccess())
    val payload = MediaLogPayload(tag)
    PacketDistributor.sendToPlayer(this, payload)
}
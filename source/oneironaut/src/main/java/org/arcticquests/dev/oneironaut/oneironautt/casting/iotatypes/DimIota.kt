package org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.loading.FMLEnvironment
import org.arcticquests.dev.oneironaut.oneironautt.ClientTime
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import java.util.UUID
import java.util.function.BiFunction

class DimIota(val dim: String) : Iota({ TYPE }) {

    constructor(world: ServerLevel) : this(world.dimension().location().toString())
    constructor(worldRegistryKey: ResourceKey<Level>) : this(worldRegistryKey.location().toString())

    override fun isTruthy() = true

    override fun toleratesOther(that: Iota): Boolean {
        if (that.type === this.type.get() && that is DimIota) {
            return this.dim == that.dim
        }
        return false
    }

    override fun hashCode(): Int {
        return dim.hashCode()
    }

    fun getDimString(): String = dim

    fun getWorldKey(): ResourceKey<Level> =
        ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dim))

    fun toWorld(server: MinecraftServer): ServerLevel? = server.getLevel(getWorldKey())

    override fun display(): Component {
        var text: Component = Component.nullToEmpty(dim).copy()
        val originalString = text.string
        text = text.copy().setStyle(text.style.withColor(TextColor.fromRgb(0x5555ff))) //default coloring
        for (transformer in styleTransformers.values) {
            text = transformer.transform(text, dim)
        }
        //don't let people change the actual string of the text server-side, it might break hexes that rely on stuff like Scrivener's
        if (Oneironaut.isServerThread() && text.string != originalString) {
            text = Component.nullToEmpty(dim).copy().setStyle(text.style)
        }
        return text
    }

    companion object {
        const val DIM_KEY = "dim_key"

        private val styleTransformers = HashMap<UUID, TextTransformer>()

        @JvmStatic
        fun registerTransformer(transformer: TextTransformer) {
            styleTransformers[transformer.uuid] = transformer
        }

        @JvmStatic
        fun getTransformer(uuid: UUID): TextTransformer? = styleTransformers[uuid]

        @JvmStatic
        fun removeTransformer(uuid: UUID) {
            styleTransformers.remove(uuid)
        }

        private val isClient = FMLEnvironment.dist == Dist.CLIENT

        private fun randomizedFormatting(original: Style): Style {
            var style = original
            if (isClient) {
                val random: RandomSource = RandomSource.create(ClientTime.getClientTime() / 5)
                if (random.nextInt(3) == 0) style = style.withBold(true)
                if (random.nextInt(4) == 0) style = style.withItalic(true)
                if (random.nextInt(4) == 0) style = style.withStrikethrough(true)
                if (random.nextInt(4) == 0) style = style.withUnderlined(true)
                //usually make it illegible
                if (random.nextInt(10) != 0) {
                    val choice = random.nextInt(3)
                    if (choice == 0) {
                        style = style.withObfuscated(true)
                    } else {
                        val fontID = when (choice) {
                            1 -> "minecraft:alt"
                            2 -> "minecraft:illageralt"
                            else -> "minecraft:uniform" //pretty sure this will never actually come up
                        }
                        style = style.withFont(ResourceLocation.parse(fontID))
                    }
                }
                return style
            } else {
                return style.withBold(true)
            }
        }

        @JvmField
        val TYPE: IotaType<DimIota> = object : IotaType<DimIota>() {
            override fun color() = 0xff5555ff.toInt()

            override fun codec(): MapCodec<DimIota> =
                Codec.STRING.fieldOf(DIM_KEY).xmap(::DimIota, DimIota::dim)

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DimIota> =
                ByteBufCodecs.STRING_UTF8.map(::DimIota, DimIota::dim).cast()
        }

        init {
            registerTransformer(TextTransformer.colorizer("minecraft:overworld", 0x00aa00))
            registerTransformer(TextTransformer.colorizer("minecraft:the_nether", 0xaa0000))
            registerTransformer(TextTransformer.colorizer("minecraft:the_end", 0xffff55))
            registerTransformer(TextTransformer(UUID.randomUUID()) { t, s ->
                if (s == "oneironaut:noosphere") {
                    t.copy().setStyle(t.style.withColor(TextColor.fromRgb(0xaa00aa)).withBold(true))
                } else t
            })
            registerTransformer(TextTransformer(UUID.randomUUID()) { t, s ->
                if (s == "oneironaut:deep_noosphere") {
                    t.copy().setStyle(randomizedFormatting(t.style.withColor(TextColor.fromRgb(0xb300de))))
                } else t
            })
        }
    }

    class TextTransformer(val uuid: UUID, private val function: BiFunction<Component, String, Component>) {
        fun transform(text: Component, worldKey: String): Component = function.apply(text, worldKey)

        companion object {
            @JvmStatic
            fun colorizer(keyToColorize: String, color: Int): TextTransformer {
                val transformer = BiFunction<Component, String, Component> { original, string ->
                    if (string == keyToColorize) {
                        original.copy().setStyle(original.style.withColor(TextColor.fromRgb(color)))
                    } else {
                        original
                    }
                }
                return TextTransformer(UUID.randomUUID(), transformer)
            }
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (javaClass != o?.javaClass) return false
        if (!super.equals(o)) return false

        o as DimIota

        if (dim != o.dim) return false

        return true
    }
}
package miyucomics.hexical.features.toast

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.ItemStack

data class ToastPayload(
    val title: Component,
    val description: Component,
    val stack: ItemStack
) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<ToastPayload> = CustomPacketPayload.Type(HexicalMain.id("toast"))
        val COMPONENT_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Component> = StreamCodec.of(
            { buf, component -> buf.writeUtf(Component.Serializer.toJson(component, buf.registryAccess())) },
            { buf -> Component.Serializer.fromJson(buf.readUtf(), buf.registryAccess())!! }
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToastPayload> = StreamCodec.composite(
            COMPONENT_STREAM_CODEC, ToastPayload::title,
            COMPONENT_STREAM_CODEC, ToastPayload::description,
            ItemStack.STREAM_CODEC, ToastPayload::stack,
            ::ToastPayload
        )
    }
}
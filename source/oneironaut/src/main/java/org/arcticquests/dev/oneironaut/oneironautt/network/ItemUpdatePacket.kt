package org.arcticquests.dev.oneironaut.oneironautt.network

import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut

class ItemUpdatePacket(val stack: ItemStack, val entityId: Int) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<ItemUpdatePacket> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "itemupdate"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ItemUpdatePacket> = StreamCodec.composite(
            ItemStack.STREAM_CODEC, ItemUpdatePacket::stack,
            ByteBufCodecs.VAR_INT, ItemUpdatePacket::entityId,
            ::ItemUpdatePacket
        )

        @JvmStatic
        fun handle(packet: ItemUpdatePacket, context: IPayloadContext) {
            context.enqueueWork {
                val level = Minecraft.getInstance().level ?: return@enqueueWork
                when (val entity = level.getEntity(packet.entityId)) {
                    is ItemFrame -> entity.item = packet.stack
                    is ItemEntity -> entity.item = packet.stack
                    else -> {}
                }
            }
        }
    }
}
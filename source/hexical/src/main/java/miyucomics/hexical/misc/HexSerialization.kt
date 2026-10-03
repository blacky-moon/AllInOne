package miyucomics.hexical.misc

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putList
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

object HexSerialization {


    fun serializeHex(hex: List<Iota>, lookup: HolderLookup.Provider): ListTag {
        val list = ListTag()
        for (iota in hex) {
            val result = IotaType.TYPED_CODEC.encodeStart(lookup.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), iota)
            result.resultOrPartial { error ->
                println("[Hexical] Failed to serialize iota: $error")
            }.ifPresent { tag ->
                list.add(tag)
            }
        }
        return list
    }
    fun deserializeHex(list: ListTag, lookup: HolderLookup.Provider): List<Iota> {
        val ops = lookup.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE)
        return list.mapNotNull { tag ->
            IotaType.TYPED_CODEC.parse(ops, tag)
                .resultOrPartial { error ->
                    println("[Hexical] Failed to deserialize iota: $error")
                }
                .orElse(null)
        }
    }

    fun backwardsCompatibleReadHex(
        holder: CompoundTag,
        key: String,
        lookup: HolderLookup.Provider
    ): List<Iota> {
        val element = holder.get(key)

        if (element is CompoundTag) {
            val ops = lookup.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE)
            val parsed = IotaType.TYPED_CODEC.parse(ops, element)
                .resultOrPartial { }
                .orElse(null)

            if (parsed is ListIota) {
                val elementData = parsed.list.toList()
                holder.remove(key)
                holder.putList(key, serializeHex(elementData, lookup))
                return elementData
            }
        }

        if (element is ListTag) {
            return deserializeHex(element, lookup)
        }
        return emptyList()
    }
}
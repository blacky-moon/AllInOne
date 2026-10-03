package miyucomics.hexcellular

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData

class StateStorage : SavedData() {
    val properties: HashMap<String, Tag> = HashMap()

    override fun save(nbt: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val ops = RegistryOps.create(NbtOps.INSTANCE, registries)
        properties.forEach { (key, element) -> nbt.put(key, element) }
        return nbt
    }

    companion object {
        private fun createFromNbt(nbt: CompoundTag, registries: HolderLookup.Provider): StateStorage {
            val state = StateStorage()
            nbt.allKeys.forEach { key -> state.properties[key] = nbt.get(key)!! }
            return state
        }

        fun getServerState(server: MinecraftServer): StateStorage {
            val dataStorage = server.getLevel(Level.OVERWORLD)!!.dataStorage
            val factory = SavedData.Factory(::StateStorage, ::createFromNbt, null)
            return dataStorage.computeIfAbsent(factory, HexcellularMain.MOD_ID)
        }

        fun listProperties(world: ServerLevel) = getServerState(world.server).properties.keys.toList()

        fun setProperty(world: ServerLevel, name: String, iota: Iota) {
            val state = getServerState(world.server)
            val ops = RegistryOps.create(NbtOps.INSTANCE, world.registryAccess())
            val encoded = IotaType.TYPED_CODEC.encodeStart(ops, iota).result()
                .orElseThrow { IllegalStateException("Failed to serialize iota for property $name") }
            state.properties[name] = encoded
            state.setDirty()
        }

        fun removeProperty(world: ServerLevel, name: String) {
            val state = getServerState(world.server)
            state.properties.remove(name)
            state.setDirty()
        }

        fun getProperty(world: ServerLevel, name: String): Iota {
            val state = getServerState(world.server)
            val tag = state.properties[name] ?: return NullIota()
            val ops = RegistryOps.create(NbtOps.INSTANCE, world.registryAccess())
            return IotaType.TYPED_CODEC.parse(ops, tag).result().orElse(NullIota())
        }
    }
}
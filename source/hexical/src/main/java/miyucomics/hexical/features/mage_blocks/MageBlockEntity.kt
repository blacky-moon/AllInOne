package miyucomics.hexical.features.mage_blocks

import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import com.mojang.datafixers.util.Pair as DfuPair // Алиас для DFU Pair

class MageBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(HexicalBlocks.MAGE_BLOCK_ENTITY.get(), pos, state) {

    val modifiers: MutableMap<ResourceLocation, MageBlockModifier> = mutableMapOf()

    fun addModifier(modifier: MageBlockModifier) {
        modifiers[modifier.type.id] = modifier
        sync()
    }

    fun clearModifiers() {
        modifiers.clear()
        sync()
    }

    fun sync() {
        setChanged()
        level?.updateNeighborsAt(getBlockPos(), HexicalBlocks.MAGE_BLOCK.get())
        level?.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(registries)

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket =
        ClientboundBlockEntityDataPacket.create(this)

    @Suppress("UNCHECKED_CAST")
    fun <T : MageBlockModifier> getModifier(type: MageBlockModifierType<T>): T =
        modifiers[type.id] as T

    fun hasModifier(type: MageBlockModifierType<*>) = modifiers.containsKey(type.id)

    fun addScryingLensLines(lines: MutableList<DfuPair<ItemStack, Component>>) {
        modifiers.values.forEach { modifier ->
            modifier.getScryingLens()?.let { lines.add(it) }
        }
    }

    override fun saveAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(nbt, registries)
        val modifiersNbt = CompoundTag()
        modifiers.forEach { (key, modifier) ->
            modifiersNbt.put(key.toString(), modifier.serialize(registries))
        }
        nbt.put("modifiers", modifiersNbt)
    }

    override fun loadAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(nbt, registries)
        val serializedModifiers = nbt.getCompound("modifiers")
        serializedModifiers.allKeys.forEach { key ->
            val modifierType = MageBlockModifierRegistry.get(ResourceLocation.parse(key))
            if (modifierType != null) {
                val modifierData = serializedModifiers.get(key)
                if (modifierData != null) {
                    modifiers[modifierType.id] = modifierType.deserialize(modifierData, registries)
                }
            }
        }
    }
}
package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class HexCandleBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.HEX_CANDLE_BLOCK_ENTITY.get(), pos, state) {
    private var pigment: FrozenPigment = FrozenPigment.DEFAULT.get()

    fun getPigment() = this.pigment
    fun setPigment(pigment: FrozenPigment) {
        this.pigment = pigment
        setChanged()
    }

    override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(nbt, provider)
        // FrozenPigment использует CODEC для сериализации
        val result = FrozenPigment.CODEC.encodeStart(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), pigment)
        result.resultOrPartial {}.ifPresent { tag ->
            nbt.put("pigment", tag)
        }
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)
        if (nbt.contains("pigment")) {
            val result = FrozenPigment.CODEC.parse(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), nbt.get("pigment"))
            result.resultOrPartial {}.ifPresent { pigment ->
                this.pigment = pigment
            }
        }
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag {
        val nbt = CompoundTag()
        saveAdditional(nbt, provider)
        return nbt
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }
}
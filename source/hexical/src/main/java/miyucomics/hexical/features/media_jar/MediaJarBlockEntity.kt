package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.addldata.ADMediaHolder
import miyucomics.hexical.features.transmuting.TransmutationResult
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.max
import kotlin.math.min

class MediaJarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY.get(), pos, state), Container, ADMediaHolder {
    private var media: Long = 0
    private var heldStack = ItemStack.EMPTY

    override fun canRecharge() = true
    override fun canProvide() = true
    override fun canConstructBattery() = true
    override fun getConsumptionPriority() = 10000
    override fun getMedia() = this.media
    override fun getMaxMedia() = MediaJarBlock.MAX_CAPACITY
    override fun setMedia(media: Long) {
        this.media = max(min(media, MediaJarBlock.MAX_CAPACITY), 0)
        setChanged()
        if (!level!!.isClientSide)
            level!!.sendBlockUpdated(worldPosition, blockState, blockState, 3)
    }

    fun insertMedia(media: Long): Long {
        val currentMedia = this.media
        setMedia(currentMedia + media)
        return this.getMedia() - currentMedia
    }
    fun withdrawMedia(media: Long): Boolean {
        if (getMedia() >= media) {
            setMedia(getMedia() - media)
            return true
        } else {
            setMedia(0)
            return false
        }
    }

    public override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(nbt, provider)
        nbt.putLong("media", media)
        if (!heldStack.isEmpty)
            nbt.put("heldStack", heldStack.save(provider, CompoundTag()))
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)
        this.media = nbt.getLong("media")

        // ИСПРАВЛЕНО: Проверяем, существует ли тег и не пустой ли он.
        // Это предотвращает ошибку "No key id in MapLike[{}]" при загрузке пустой банки.
        val heldTag = nbt.getCompound("heldStack")
        this.heldStack = if (heldTag.isEmpty) {
            ItemStack.EMPTY
        } else {
            ItemStack.parse(provider, heldTag).orElse(ItemStack.EMPTY)
        }
    }

    // Container methods
    override fun getContainerSize() = 1
    override fun getMaxStackSize() = 1
    override fun stillValid(player: Player) = false
    override fun getItem(slot: Int): ItemStack = if (slot == 0) heldStack else ItemStack.EMPTY
    override fun isEmpty() = heldStack.isEmpty

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        if (slot == 0) {
            setChanged()
            return heldStack.split(amount)
        }
        return ItemStack.EMPTY
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == 0) {
            val originalHeld = heldStack
            heldStack = ItemStack.EMPTY
            setChanged()
            return originalHeld
        }
        return ItemStack.EMPTY
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot != 0)
            return
        if (level == null)
            return

        when (val result = TransmutingHelper.transmuteItem(level!!, stack, getMedia(), ::insertMedia, ::withdrawMedia)) {
            is TransmutationResult.AbsorbedMedia -> {
                level!!.playSound(null, worldPosition, HexicalSounds.AMETHYST_MELT.get(), SoundSource.BLOCKS, 1f, 1f)
                heldStack = stack
            }
            is TransmutationResult.TransmutedItems -> {
                val outputs: MutableList<ItemStack> = ArrayList(result.output)
                heldStack = outputs.removeAt(0).copy()
                val spawnPosition = worldPosition.below().getCenter()
                outputs.forEach { level!!.addFreshEntity(ItemEntity(level!!, spawnPosition.x, spawnPosition.y, spawnPosition.z, it.copy(), 0.0, 0.0, 0.0)) }
                level!!.playSound(null, worldPosition, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
            }
            is TransmutationResult.RefilledHolder -> {
                level!!.playSound(null, worldPosition, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
                heldStack = stack
            }
            is TransmutationResult.Pass -> {
                heldStack = stack
            }
        }

        setChanged()
    }

    override fun clearContent() {
        heldStack = ItemStack.EMPTY
        setChanged()
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
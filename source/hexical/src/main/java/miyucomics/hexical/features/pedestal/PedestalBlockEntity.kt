package miyucomics.hexical.features.pedestal

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*

class PedestalBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.PEDESTAL_BLOCK_ENTITY.get(), pos, state), Container, ADIotaHolder {
    var heldStack: ItemStack = ItemStack.EMPTY
    private var heldEntity: ItemEntity? = null
    private var persistentUUID: UUID? = null

    val normalVector: Direction = blockState.getValue(PedestalBlock.FACING)

    fun onBlockBreak() {
        heldEntity?.discard()
    }

    fun onBlockPlace() {
        this.persistentUUID = UUID.randomUUID()
        val position = Vec3.atCenterOf(this.blockPos)
        this.heldEntity = ItemEntity(this.level!!, position.x, position.y, position.z, this.heldStack)
        this.heldEntity!!.uuid = this.persistentUUID!!
        configureItemEntity()
        this.level?.addFreshEntity(this.heldEntity!!)
    }

    fun onUse(player: Player, hand: InteractionHand) {
        val playerStack = player.getItemInHand(hand)
        if (ItemStack.isSameItemSameComponents(this.heldStack, playerStack)) {
            if (!level!!.isClientSide) {
                val amount = minOf(this.heldStack.maxStackSize - this.heldStack.count, playerStack.count)
                this.heldStack.grow(amount)
                playerStack.shrink(amount)
                updateItemEntity()
            }
            setChanged()
            return
        }
        player.setItemInHand(hand, this.heldStack)
        this.heldStack = playerStack
        updateItemEntity()
        setChanged()
    }

    fun tick(level: Level) {
        if (level.isClientSide) return
        tryFillItemEntity()
        updateItemStack()
        suckOrMergeItems()
        updateItemEntity()
        configureItemEntity()
    }

    private fun suckOrMergeItems() {
        val bounds = AABB(this.blockPos).inflate(-0.1)
        level!!.getEntitiesOfClass(ItemEntity::class.java, bounds) {
            it.uuid != persistentUUID && !it.isRemoved && !it.hasPickUpDelay()
        }.forEach { item ->
            val stack = item.item
            if (this.heldStack.isEmpty) {
                this.heldStack = stack.copyAndClear()
                item.discard()
                updateItemEntity()
                setChanged()
                return
            }
            if (ItemStack.isSameItemSameComponents(this.heldStack, stack)) {
                val toTransfer = minOf(this.heldStack.maxStackSize - this.heldStack.count, stack.count)
                this.heldStack.grow(toTransfer)
                stack.shrink(toTransfer)
                updateItemEntity()
                setChanged()
                return
            }
        }
    }

    fun modifyImage(image: CastingImage): CastingImage {
        val data = IXplatAbstractions.INSTANCE.findDataHolder(heldStack) ?: return image
        val iota = data.readIota() ?: return image
        return if (image.parenCount == 0) {
            image.copy(stack = image.stack + iota)
        } else {
            image.copy(parenthesized = image.parenthesized + ParenthesizedIota(iota, false))
        }
    }

    // === Реализация Container ===
    override fun getContainerSize() = 1
    override fun isEmpty() = heldStack.isEmpty

    override fun getItem(slot: Int): ItemStack = if (slot == 0) heldStack else ItemStack.EMPTY

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot == 0) {
            heldStack = stack
            updateItemEntity()
            setChanged()
        }
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        if (slot == 0) {
            val removed = heldStack.split(amount)
            updateItemEntity()
            setChanged()
            return removed
        }
        return ItemStack.EMPTY
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == 0) {
            val removed = heldStack
            heldStack = ItemStack.EMPTY
            updateItemEntity()
            setChanged()
            return removed
        }
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player) = false

    override fun clearContent() {
        heldStack = ItemStack.EMPTY
        updateItemEntity()
        setChanged()
    }

    // === Внутренняя логика ===
    private fun updateItemStack() {
        if (level!!.isClientSide) return
        if (this.heldEntity == null || this.heldEntity!!.isRemoved) {
            heldStack = ItemStack.EMPTY
            setChanged()
            return
        }
        this.heldStack = this.heldEntity!!.item
        setChanged()
    }

    private fun updateItemEntity() {
        if (level!!.isClientSide) return
        if (this.heldStack.isEmpty) {
            this.heldEntity?.discard()
            this.heldEntity = null
            setChanged()
            return
        }
        val serverLevel = this.level as? ServerLevel ?: return
        if (this.persistentUUID == null) this.persistentUUID = UUID.randomUUID()
        if (this.heldEntity == null || (this.heldEntity!!.isRemoved && !tryFillItemEntity())) {
            val position = Vec3.atCenterOf(this.blockPos)
            this.heldEntity = ItemEntity(serverLevel, position.x, position.y, position.z, this.heldStack)
            this.heldEntity!!.uuid = this.persistentUUID!!
            configureItemEntity()
            serverLevel.addFreshEntity(this.heldEntity!!)
        }
        this.heldEntity?.item = this.heldStack
    }

    fun tryFillItemEntity(): Boolean {
        if (this.persistentUUID == null) this.persistentUUID = UUID.randomUUID()
        if (this.heldEntity != null) return true

        val serverLevel = this.level as? ServerLevel ?: return true
        val existing = serverLevel.getEntity(persistentUUID!!)
        if (existing is ItemEntity) {
            this.heldEntity = existing
            return true
        }
        return false
    }

    fun configureItemEntity() {
        this.heldEntity?.let {
            val pos = getItemPosition()
            it.setPos(pos.x, pos.y, pos.z)
            it.boundingBox = AABB(pos, pos).inflate(0.25)
            it.noPhysics = true
            it.setUnlimitedLifetime()
            it.isNoGravity = true

            it.isInvulnerable = true
            it.deltaMovement = Vec3.ZERO
            it.setPickUpDelay(1000000)
        }
    }

    // === NBT и Сеть ===
    override fun loadAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(nbt, registries)
        heldStack = ItemStack.parseOptional(registries, nbt.getCompound("item"))
        if (nbt.hasUUID("persistent_uuid"))
            persistentUUID = nbt.getUUID("persistent_uuid")
    }

    override fun saveAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(nbt, registries)
        if (!heldStack.isEmpty)
            nbt.put("item", heldStack.save(registries, CompoundTag()))
        persistentUUID?.let { nbt.putUUID("persistent_uuid", it) }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().also { saveAdditional(it, registries) }
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    override fun setChanged() {
        level?.sendBlockUpdated(blockPos, blockState, blockState, 3)
        super.setChanged()
    }

    // === Hex Casting ADIotaHolder ===
    override fun writeable(): Boolean = true
    override fun readIota(): Iota? = IXplatAbstractions.INSTANCE.findDataHolder(this.heldStack)?.readIota()
    override fun writeIota(iota: Iota?, simulate: Boolean): Boolean =
        IXplatAbstractions.INSTANCE.findDataHolder(this.heldStack)?.writeIota(iota, simulate) ?: false

    fun getItemPosition(): Vec3 {
        val normal = normalVector.step()
        return Vec3.atCenterOf(this.blockPos)
            .add(Vec3(normal.x().toDouble(), normal.y().toDouble(), normal.z().toDouble()).scale(HEIGHT))
    }
    companion object {
        const val HEIGHT = 0.75
    }
}
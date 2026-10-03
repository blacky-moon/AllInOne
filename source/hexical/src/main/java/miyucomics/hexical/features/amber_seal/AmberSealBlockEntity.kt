package miyucomics.hexical.features.amber_seal

import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalBlocks.AMBER_SEAL_BLOCK
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.ChestType

class AmberSealBlockEntity(pos: BlockPos, state: BlockState, var encasedState: BlockState) :
    BlockEntity(HexicalBlocks.AMBER_SEAL_BLOCK_ENTITY.get(), pos, state) {

    constructor(pos: BlockPos, state: BlockState) : this(pos, state, Blocks.AIR.defaultBlockState())

    var encasedEntity: BlockEntity? = null

    companion object {
        // Предохранитель от переполнения стека при (де)сериализации/рендере
        // глубоко вложенных печатей. Проверяется здесь, а не только в спелле,
        // чтобы закрыть любые пути получения вложенности (предметы, крафт и т.д.).
        const val MAX_NESTING_DEPTH = 16

        // Счётчики текущей глубины рекурсии при loadStatic/loadAdditional и
        // saveWithFullMetadata/saveAdditional. Загрузка/сохранение одного
        // дерева BlockEntity идёт синхронно в одном потоке, поэтому
        // ThreadLocal-счётчика достаточно, если аккуратно его инкрементировать
        // и декрементировать (finally) вокруг рекурсивного вызова.
        private val loadDepth = ThreadLocal.withInitial { 0 }
        private val saveDepth = ThreadLocal.withInitial { 0 }

        @JvmStatic
        fun nestingDepthOf(entity: BlockEntity?): Int {
            var depth = 0
            var current = entity
            while (current is AmberSealBlockEntity) {
                depth++
                current = current.encasedEntity
            }
            return depth
        }
    }

    fun init(state: BlockState, blockEntity: BlockEntity?) {
        // Не позволяем создать печать, которая инкапсулирует саму себя
        // (защита от циклической ссылки, приводящей к StackOverflowError).
        require(blockEntity !== this) {
            "AmberSealBlockEntity cannot encase itself"
        }
        // Не позволяем превысить допустимую глубину вложенности вне
        // зависимости от того, каким путём формируется структура
        // (заклинание, предмет из инвентаря, размещение блока и т.п.).
        val depth = nestingDepthOf(blockEntity)
        require(depth < MAX_NESTING_DEPTH) {
            "AmberSealBlockEntity nesting depth exceeds $MAX_NESTING_DEPTH"
        }

        this.encasedState = state
        this.encasedEntity = blockEntity
        if (this.encasedState.hasProperty(BlockStateProperties.CHEST_TYPE))
            this.encasedState = this.encasedState.setValue(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE)
        this.encasedEntity?.setLevel(this.level!!)
    }

    override fun loadAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(nbt, registries)
        // readBlockState требует HolderGetter<Block>, получаем его из registries
        val blockGetter = registries.lookupOrThrow(Registries.BLOCK)
        this.encasedState = NbtUtils.readBlockState(blockGetter, nbt.getCompound("EncasedBlockState"))

        if (nbt.contains("EncasedBlockEntity")) {
            // Данные могли попасть в мир до появления защиты от чрезмерной
            // вложенности (или быть подделаны вручную), поэтому ограничиваем
            // глубину и при загрузке, а не только при наложении новой печати —
            // иначе простая загрузка чанка с уже испорченными данными могла
            // бы уронить сервер тем же StackOverflowError.
            // BlockEntity.loadStatic ниже сам рекурсивно вызовет
            // loadAdditional для вложенных печатей, поэтому считаем глубину
            // через ThreadLocal-счётчик, а не параметром функции.
            val depth = loadDepth.get()
            if (depth >= MAX_NESTING_DEPTH) {
                this.encasedEntity = null
            } else {
                loadDepth.set(depth + 1)
                try {
                    this.encasedEntity = BlockEntity.loadStatic(
                        this.blockPos,
                        this.encasedState,
                        nbt.getCompound("EncasedBlockEntity"),
                        registries
                    )
                } finally {
                    loadDepth.set(depth)
                }
            }
            this.encasedEntity?.setLevel(level!!)
        }
    }

    override fun saveAdditional(nbt: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(nbt, registries)
        // writeBlockState в 1.21.1 принимает ТОЛЬКО BlockState (без registries)
        nbt.put("EncasedBlockState", NbtUtils.writeBlockState(this.encasedState))

        // Тот же ThreadLocal-счётчик, что и в loadAdditional: saveWithFullMetadata
        // ниже рекурсивно вызовет saveAdditional для вложенных печатей, так что
        // без ограничения глубины уже существующая (например, созданная до
        // этого фикса) чрезмерно вложенная структура могла бы уронить сервер
        // тем же StackOverflowError при первом же автосохранении.
        val depth = saveDepth.get()
        if (depth >= MAX_NESTING_DEPTH) return
        saveDepth.set(depth + 1)
        try {
            this.encasedEntity?.let {
                nbt.put("EncasedBlockEntity", it.saveWithFullMetadata(registries))
            }
        } finally {
            saveDepth.set(depth)
        }
    }

    fun toItem(): ItemStack {
        val currentLevel = level ?: return ItemStack(AMBER_SEAL_BLOCK.get().asItem())
        val stack = ItemStack(AMBER_SEAL_BLOCK.get().asItem())
        val beData = CompoundTag()
        beData.putString("id", this.type.toString())
        saveAdditional(beData, currentLevel.registryAccess())

        stack.set(
            DataComponents.BLOCK_ENTITY_DATA,
            CustomData.of(beData)
        )
        return stack
    }
    fun onPlaced(ctx: BlockPlaceContext) {
        this.encasedState.block.getStateForPlacement(ctx)?.let { placementState ->
            val newState = AmberSealBlock.copyRotationParameters(this.encasedState, placementState)
            if (this.encasedState != newState) {
                this.encasedState = newState
                this.setChanged()
            }
        }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        saveWithoutMetadata(registries)

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket =
        ClientboundBlockEntityDataPacket.create(this)
}
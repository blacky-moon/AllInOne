package miyucomics.hexical.features.prestidigitation.handlers

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf
import at.petrak.hexcasting.common.lib.HexBlocks
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandler
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandlerBlock
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BeehiveBlockEntity
import net.minecraft.world.level.block.entity.BellBlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.ComparatorMode
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace

object PrestidigitationHandlersBlock {
    // Рефлексия для приватных полей
    private val axeStrippablesField by lazy {
        AxeItem::class.java.getDeclaredField("STRIPPABLES").apply { isAccessible = true }
    }
    private val shovelPathStatesField by lazy {
        ShovelItem::class.java.getDeclaredField("FLATTENABLES").apply { isAccessible = true }
    }
    private val furnaceLitTimeField by lazy {
        AbstractFurnaceBlockEntity::class.java.getDeclaredField("litTime").apply { isAccessible = true }
    }
    private val furnaceBurnTimeField by lazy {
        AbstractFurnaceBlockEntity::class.java.getDeclaredField("burnTime").apply { isAccessible = true }
    }
    // Рефлексия для приватного метода DragonEggBlock.teleport
    private val dragonEggTeleportMethod by lazy {
        DragonEggBlock::class.java.getDeclaredMethod("teleport", BlockState::class.java, Level::class.java, BlockPos::class.java)
            .apply { isAccessible = true }
    }
    // Рефлексия для приватного метода Llama.spit
    private val llamaSpitMethod by lazy {
        net.minecraft.world.entity.animal.horse.Llama::class.java.getDeclaredMethod(
            "spit", net.minecraft.world.entity.LivingEntity::class.java
        ).apply { isAccessible = true }
    }

    @Suppress("UNCHECKED_CAST")
    private val strippables: Map<Block, Block>
        get() = axeStrippablesField.get(null) as Map<Block, Block>

    @Suppress("UNCHECKED_CAST")
    private val pathStates: Map<Block, BlockState>
        get() = shovelPathStatesField.get(null) as Map<Block, BlockState>

    fun init(register: (PrestidigitationHandler) -> Unit) {
        // Comparator toggle
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(Blocks.COMPARATOR)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                val mode = state.getValue(BlockStateProperties.MODE_COMPARATOR)
                setBlockState(env, pos, state.setValue(BlockStateProperties.MODE_COMPARATOR,
                    if (mode == ComparatorMode.COMPARE) ComparatorMode.SUBTRACT else ComparatorMode.COMPARE
                ))
            }
        })

        // Pumpkin → Carved Pumpkin
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(Blocks.PUMPKIN)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                setBlockState(env, pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
                    Direction.entries.random()
                ))
            }
        })

        // Strip wood
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                strippables.containsKey(getBlock(env, pos))
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val block = getBlock(env, pos)
                val strippedBlock = strippables[block] ?: return
                val state = getBlockState(env, pos)
                val axis = if (state.hasProperty(BlockStateProperties.AXIS)) state.getValue(BlockStateProperties.AXIS) else Direction.Axis.Y
                setBlockState(env, pos, strippedBlock.defaultBlockState().setValue(BlockStateProperties.AXIS, axis))
            }
        })

        // Path
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                pathStates.containsKey(getBlock(env, pos))
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val block = getBlock(env, pos)
                val pathState = pathStates[block] ?: return
                setBlockState(env, pos, pathState)
            }
        })

        // Buttons
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(BlockTags.BUTTONS)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                val block = state.block as ButtonBlock
                block.press(state, env.world, pos,env.caster)
                env.world.playSound(null, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1f, 1f)
                env.world.gameEvent(null, GameEvent.BLOCK_ACTIVATE, pos)
            }
        })

        // Fire extinguish
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(BlockTags.FIRE)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                env.world.removeBlock(pos, false)
            }
        })

        // Soul fire on soul fire base
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(BlockTags.SOUL_FIRE_BASE_BLOCKS) &&
                        getBlockState(env, pos.above()).isAir
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                setBlockState(env, pos.above(), Blocks.SOUL_FIRE.defaultBlockState())
            }
        })

        // Pressure plates
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(BlockTags.PRESSURE_PLATES)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                val powered = state.getValue(BlockStateProperties.POWERED)
                setBlockState(env, pos, state.setValue(BlockStateProperties.POWERED, !powered))
            }
        })

        // Cauldrons → Empty cauldron
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(BlockTags.CAULDRONS)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                setBlockState(env, pos, Blocks.CAULDRON.defaultBlockState())
            }
        })

        // Toggle lit state (candles, campfires)
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
                val state = getBlockState(env, pos)
                return state.`is`(BlockTags.CANDLES) || state.`is`(BlockTags.CANDLE_CAKES) || state.`is`(BlockTags.CAMPFIRES)
            }
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                val lit = state.getValue(BlockStateProperties.LIT)
                setBlockState(env, pos, state.setValue(BlockStateProperties.LIT, !lit))
            }
        })

        // Doors, trapdoors, fence gates
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
                val state = getBlockState(env, pos)
                return state.`is`(BlockTags.DOORS) || state.`is`(BlockTags.TRAPDOORS) || state.`is`(BlockTags.FENCE_GATES)
            }
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                val open = state.getValue(BlockStateProperties.OPEN)
                setBlockState(env, pos, state.setValue(BlockStateProperties.OPEN, !open))
            }
        })

        // Beehive with honey level 5
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
                val state = getBlockState(env, pos)
                return state.`is`(BlockTags.BEEHIVES) && state.getValue(BlockStateProperties.LEVEL_HONEY) == 5
            }
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                env.world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1f, 1f)
                BeehiveBlock.dropHoneycomb(env.world, pos)
                env.world.gameEvent(null, GameEvent.SHEAR, pos)
            }
        })

        // Note block
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(Blocks.NOTE_BLOCK)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                env.world.blockEvent(pos, Blocks.NOTE_BLOCK, 0, 0)
                env.world.gameEvent(null, GameEvent.NOTE_BLOCK_PLAY, pos)
            }
        })

        // Bell
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(Blocks.BELL)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                val bell = state.block as BellBlock
                val facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                val attachment = state.getValue(BlockStateProperties.ATTACH_FACE)
                val ringDirection = when (attachment) {
                    AttachFace.WALL -> facing.clockWise
                    else -> facing
                }
                val bellEntity = env.world.getBlockEntity(pos) as? BellBlockEntity
                // Вызываем onHit вместо triggerEvent
                bellEntity?.onHit(ringDirection)
                env.world.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 2f, 1f)
                env.world.gameEvent(env.castingEntity, GameEvent.BLOCK_CHANGE, pos)
            }
        })

        // Dispenser
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlock(env, pos) is DispenserBlock
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                env.world.blockEvent(pos, Blocks.DISPENSER, 1, 0)
            }
        })

        // TNT prime – используем статический explode
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(Blocks.TNT)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                TntBlock.explode(env.world, pos)
                env.world.removeBlock(pos, false)
            }
        })

        // Akashic Bookshelf read – используем геттер iota напрямую
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                env.castingEntity is ServerPlayer && getBlockState(env, pos).`is`(HexBlocks.AKASHIC_BOOKSHELF)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val caster = env.castingEntity as ServerPlayer
                val bookshelf = env.world.getBlockEntity(pos) as? BlockEntityAkashicBookshelf ?: return
                val iota = bookshelf.iota ?: return
                CastingUtils.giveIota(caster, iota)
            }
        })

        // Impetus start
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                env.castingEntity is ServerPlayer && getBlockState(env, pos).`is`(HexTags.Blocks.IMPETI)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val impetus = env.world.getBlockEntity(pos) as? BlockEntityAbstractImpetus ?: return
                impetus.startExecution(env.castingEntity as ServerPlayer)
            }
        })

        // Furnace boost
        register(PrestidigitationHandlerBlock.blockEntity(AbstractFurnaceBlockEntity::class.java) { world, pos, furnace ->
            furnaceLitTimeField.setInt(furnace, 200)
            furnaceBurnTimeField.setInt(furnace, 200)
            world.setBlock(pos, world.getBlockState(pos).setValue(AbstractFurnaceBlock.LIT, true), 3)
            furnace.setChanged()
        })

        // Dragon egg teleport – рефлексия приватного метода
        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                getBlockState(env, pos).`is`(Blocks.DRAGON_EGG)
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val state = getBlockState(env, pos)
                dragonEggTeleportMethod.invoke(state.block, state, env.world, pos)
            }
        })
    }
}
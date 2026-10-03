package miyucomics.hexical.features.dyes.entity

import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import miyucomics.hexical.features.dyes.DyeOption
import miyucomics.hexical.features.dyes.DyeingUtils
import miyucomics.hexical.features.specklikes.FigureSpecklike
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import java.util.*

object DyeingEntityRegistry : InitHook() {
    private val handlers = mutableListOf<DyeEntityHandler<*>>()

    // В 1.21.1 setCollarColor стал private, используем Reflection
    private val catSetCollarColorMethod = Cat::class.java.getDeclaredMethod("setCollarColor", DyeColor::class.java).apply { isAccessible = true }
    private val wolfSetCollarColorMethod = Wolf::class.java.getDeclaredMethod("setCollarColor", DyeColor::class.java).apply { isAccessible = true }

    override fun init() {
        register(object : DyeEntityHandler<ItemEntity>(ItemEntity::class.java) {
            override fun extraConditions(entity: ItemEntity, dye: DyeOption) = DyeingUtils.getResult(entity.item, dye) != null
            override fun affect(entity: ItemEntity, dye: DyeOption) {
                val stack = entity.item.copy()
                entity.item = DyeingUtils.getResult(stack, dye) ?: stack
            }
        })

        register(object : DyeEntityHandler<ItemEntity>(ItemEntity::class.java) {
            override fun extraConditions(entity: ItemEntity, dye: DyeOption): Boolean {
                val stack = entity.item
                return stack.item is BlockItem && DyeingUtils.getResult((stack.item as BlockItem).block, dye) != null
            }
            override fun affect(entity: ItemEntity, dye: DyeOption) {
                val stack = entity.item
                val result = DyeingUtils.getResult((stack.item as BlockItem).block, dye)!!
                entity.item = ItemStack(result.asItem(), stack.count)
            }
        })

        register(object : DyeEntityHandler<Cat>(Cat::class.java) {
            override fun extraConditions(entity: Cat, dye: DyeOption): Boolean = dye != DyeOption.UNCOLORED
            override fun affect(entity: Cat, dye: DyeOption) {
                catSetCollarColorMethod.invoke(entity, dye.dyeColor!!)
            }
        })

        register(object : DyeEntityHandler<Wolf>(Wolf::class.java) {
            override fun extraConditions(entity: Wolf, dye: DyeOption): Boolean = dye != DyeOption.UNCOLORED
            override fun affect(entity: Wolf, dye: DyeOption) {
                wolfSetCollarColorMethod.invoke(entity, dye.dyeColor!!)
            }
        })

        register(object : DyeEntityHandler<Sheep>(Sheep::class.java) {
            override fun extraConditions(entity: Sheep, dye: DyeOption): Boolean = dye != DyeOption.UNCOLORED
            override fun affect(entity: Sheep, dye: DyeOption) {
                entity.color = dye.dyeColor
            }
        })

        register(object : DyeEntityHandler<FigureSpecklike>(FigureSpecklike::class.java) {
            override fun extraConditions(entity: FigureSpecklike, dye: DyeOption): Boolean = dye != DyeOption.UNCOLORED
            override fun affect(entity: FigureSpecklike, dye: DyeOption) {
                entity.setPigment(FrozenPigment(ItemStack(HexItems.DYE_PIGMENTS[dye.dyeColor]!!), UUID.randomUUID()))
            }
        })

        register(object : DyeEntityHandler<Shulker>(Shulker::class.java) {
            override fun affect(entity: Shulker, dye: DyeOption) {
                // Исправлено: Optional.ofNullable вместо невалидного синтаксиса
                entity.variant = Optional.ofNullable(dye.dyeColor)
            }
        })
    }

    fun register(handler: DyeEntityHandler<*>) {
        handlers += handler
    }

    fun resolve(entity: Entity, dye: DyeOption) = handlers.firstOrNull { it.canAffectEntity(entity, dye) }
}
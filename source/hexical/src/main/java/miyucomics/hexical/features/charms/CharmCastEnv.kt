package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack

/**
 * ВАЖНО: этот класс намеренно НЕ хранит ItemStack как поле. Все операции с
 * заряженным предметом идут через CharmHandle.resolve(), который заново
 * находит актуальный стек в инвентаре игрока перед каждым обращением. Это
 * защищает от ситуации, когда хекс во время своего выполнения перемещает,
 * роняет или иным образом трогает исходный предмет — списание медиа в таком
 * случае либо попадёт в реальный (перемещённый) предмет, либо, если предмет
 * пропал совсем, каст корректно не спишет ничего и провалится, вместо того
 * чтобы стать бесплатным.
 */
class CharmCastEnv(
    caster: ServerPlayer,
    castingHand: InteractionHand,
    private val handle: CharmHandle
) : PlayerBasedCastEnv(caster, castingHand) {

    override fun extractMediaEnvironment(cost: Long, simulate: Boolean): Long {
        if (caster.isCreative) return 0

        val stack = handle.resolveCharmed() ?: return cost // предмета/чарма больше нет — платить нечем

        var costLeft = cost
        val currentMedia = CharmUtilities.getMedia(stack)
        val mediaToDeduct = minOf(currentMedia, costLeft)
        costLeft -= mediaToDeduct
        if (!simulate)
            CharmUtilities.deductMedia(stack, mediaToDeduct)
        return costLeft
    }

    override fun getCastingHand(): InteractionHand = this.castingHand
    override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

    /**
     * Актуальный ItemStack заряженного предмета на ЭТОТ момент времени. Всегда
     * резолвится заново — НЕ кешируйте возвращённое значение между шагами каста,
     * вызывайте getCurrentStack() каждый раз заново непосредственно перед использованием.
     * Возвращает ItemStack.EMPTY, если предмет пропал (выброшен, передан, чарм снят).
     */
    fun getCurrentStack(): ItemStack = handle.resolveCharmed() ?: ItemStack.EMPTY

    fun getInternalStorage(): Iota =
        handle.resolveCharmed()?.let { CharmUtilities.getInternalStorage(it) } ?: NullIota()

    fun setInternalStorage(iota: Iota) {
        handle.resolveCharmed()?.let { CharmUtilities.setInternalStorage(it, iota) }
    }
}
package miyucomics.hexical.features.charms

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import java.util.UUID

/**
 * Единственный легитимный способ ссылаться на заряженный предмет чарма на всём
 * протяжении каста. НИКОГДА не храните ItemStack напрямую в состоянии долгоживущих
 * объектов (CharmCastEnv и т.п.) — только CharmHandle.
 *
 * Причина: выполнение хекса (CastingVM.queueExecuteAndWrapIotas) может внутри себя
 * трогать инвентарь игрока (перекладывать, ронять, передавать предметы). Если код
 * держит захваченную Java-ссылку на ItemStack, полученную до начала каста, эта
 * ссылка может "протухнуть" (перестать соответствовать реальному предмету в
 * инвентаре), и любые операции над ней (например, списание медиа) ничего не
 * меняют в реальном мире — то есть каст фактически становится бесплатным.
 *
 * CharmHandle решает это, идентифицируя предмет стабильным UUID (records в NBT
 * чарма), а не объектной ссылкой, и резолвя актуальный ItemStack заново перед
 * каждой операцией.
 */
class CharmHandle private constructor(
    private val player: ServerPlayer,
    private val charmId: UUID,
    private var lastKnownHand: InteractionHand?
) {
    /**
     * Резолвит актуальный ItemStack по charmId. Возвращает null, если предмет
     * с этим id нигде не найден у игрока (выброшен, передан, уничтожен и т.п.).
     */
    fun resolve(): ItemStack? {
        lastKnownHand?.let { hand ->
            val stack = player.getItemInHand(hand)
            if (CharmUtilities.getCharmId(stack) == charmId) return stack
        }

        for (hand in InteractionHand.entries) {
            val stack = player.getItemInHand(hand)
            if (CharmUtilities.getCharmId(stack) == charmId) {
                lastKnownHand = hand
                return stack
            }
        }

        val found = CharmUtilities.findByCharmId(player, charmId)
        if (found != null) lastKnownHand = null
        return found
    }

    /** Удобный шорткат: резолвит стек и сразу проверяет, что чарм на нём ещё существует. */
    fun resolveCharmed(): ItemStack? = resolve()?.takeIf { CharmUtilities.isStackCharmed(it) }

    companion object {
        /**
         * Создаёт handle для предмета, уже находящегося в руке игрока. Присваивает
         * charm_id, если он ещё не был назначен. Возвращает null, если предмет не
         * зачарован (не должно происходить в норме — вызывающий код обязан
         * проверить isStackCharmed заранее).
         */
        @JvmStatic
        fun from(player: ServerPlayer, hand: InteractionHand, stack: ItemStack): CharmHandle? {
            val id = CharmUtilities.getOrAssignCharmId(stack) ?: return null
            return CharmHandle(player, id, hand)
        }
    }
}
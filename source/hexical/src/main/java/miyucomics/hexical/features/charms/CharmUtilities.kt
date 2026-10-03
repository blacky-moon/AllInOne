package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.mediaBarColor
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.network.chat.TextColor
import java.util.UUID

object CharmUtilities {
    val CHARMED_COLOR: TextColor = TextColor.fromRgb(0xe83d72)
    private const val CHARM_ID_KEY = "charm_id"

    @JvmStatic
    fun getUseableCharmedItems(player: Player): List<Pair<InteractionHand, ItemStack>> {
        val options = mutableListOf<Pair<InteractionHand, ItemStack>>()
        if (isStackCharmed(player.getItemInHand(InteractionHand.MAIN_HAND)))
            options.add(Pair(InteractionHand.MAIN_HAND, player.getItemInHand(InteractionHand.MAIN_HAND)))
        if (isStackCharmed(player.getItemInHand(InteractionHand.OFF_HAND)))
            options.add(Pair(InteractionHand.OFF_HAND, player.getItemInHand(InteractionHand.OFF_HAND)))
        return options
    }

    @JvmStatic
    fun shouldIntercept(stack: ItemStack, button: Int, sneaking: Boolean): Boolean {
        val charmed = getCompound(stack)
        val inputs = if (sneaking) charmed.getIntArray("sneak_inputs") else charmed.getIntArray("normal_inputs")
        return inputs.contains(button)
    }

    fun removeCharm(stack: ItemStack) {
        val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return
        val tag = customData.copyTag()
        tag.remove("charmed")
        if (tag.isEmpty) {
            stack.remove(DataComponents.CUSTOM_DATA)
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
        if (stack.item is CurioItem) stack.shrink(1)
    }

    fun deductMedia(stack: ItemStack, cost: Long) {
        val oldMedia = getMedia(stack)
        if (oldMedia <= cost) {
            removeCharm(stack)
        } else {
            val compound = getCompound(stack)
            compound.putLong("media", oldMedia - cost)
            setCompound(stack, compound)
        }
    }

    @JvmStatic
    fun isStackCharmed(stack: ItemStack): Boolean {
        val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return customData.copyTag().contains("charmed")
    }

    fun getCompound(stack: ItemStack): CompoundTag {
        val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return CompoundTag()
        val tag = customData.copyTag()
        return if (tag.contains("charmed")) tag.getCompound("charmed") else CompoundTag()
    }

    // В 1.21.1 требуется HolderLookup.Provider для десериализации Iota
    fun getHex(stack: ItemStack, lookup: HolderLookup.Provider): List<Iota> =
        HexSerialization.backwardsCompatibleReadHex(getCompound(stack), "hex", lookup)

    @JvmStatic
    fun getMedia(stack: ItemStack): Long = getCompound(stack).getLong("media")

    @JvmStatic
    fun getMaxMedia(stack: ItemStack): Long = getCompound(stack).getLong("max_media")

    @JvmStatic
    fun getBarColor(stack: ItemStack): Int {
        val compound = getCompound(stack)
        if (!compound.contains("media_bar_color"))
            return mediaBarColor(getMedia(stack), getMaxMedia(stack))
        return compound.getInt("media_bar_color")
    }

    fun getInternalStorage(stack: ItemStack): Iota {
        val nbt = getCompound(stack)
        if (nbt.contains("storage")) {
            val storageNbt = nbt.getCompound("storage")
            val result = IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, storageNbt)
            return result.resultOrPartial {}.orElse(NullIota())
        }
        return NullIota()
    }

    fun setInternalStorage(stack: ItemStack, iota: Iota) {
        val compound = getCompound(stack)
        val result = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, iota)
        result.resultOrPartial {}.ifPresent { tag ->
            compound.put("storage", tag)
            setCompound(stack, compound)
        }
    }

    fun setCompound(stack: ItemStack, compound: CompoundTag) {
        val customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        val tag = customData.copyTag()
        tag.put("charmed", compound)
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
    }

    // ---------------------------------------------------------------------
    // Стабильная идентификация чармленного предмета (не зависит от Java-ссылки
    // ItemStack, которая может "протухнуть" во время исполнения хекса).
    // ---------------------------------------------------------------------

    /**
     * Возвращает UUID чарма, если он уже присвоен, иначе null.
     * Не мутирует стек.
     */
    @JvmStatic
    fun getCharmId(stack: ItemStack): UUID? {
        if (!isStackCharmed(stack)) return null
        val compound = getCompound(stack)
        if (!compound.hasUUID(CHARM_ID_KEY)) return null
        return compound.getUUID(CHARM_ID_KEY)
    }

    /**
     * Возвращает UUID чарма, присваивая новый и записывая его в NBT, если он ещё не назначен.
     * Вызывать один раз в момент наложения чарма на предмет (или лениво при первом использовании).
     */
    @JvmStatic
    fun getOrAssignCharmId(stack: ItemStack): UUID? {
        if (!isStackCharmed(stack)) return null
        val compound = getCompound(stack)
        if (!compound.hasUUID(CHARM_ID_KEY)) {
            compound.putUUID(CHARM_ID_KEY, UUID.randomUUID())
            setCompound(stack, compound)
        }
        return compound.getUUID(CHARM_ID_KEY)
    }

    /**
     * Ищет предмет с данным charm_id у игрока: сперва в обеих руках, затем во всём
     * основном инвентаре. Если у тебя есть Curios/Trinkets API — добавь сканирование
     * его слотов сюда же (см. TODO ниже).
     *
     * Возвращает null, если предмет нигде не найден (выброшен, передан другому игроку,
     * уничтожен и т.п.) — в этом случае вызывающий код обязан считать, что "платить нечем".
     */
    @JvmStatic
    fun findByCharmId(player: Player, id: UUID): ItemStack? {
        for (hand in InteractionHand.entries) {
            val stack = player.getItemInHand(hand)
            if (getCharmId(stack) == id) return stack
        }

        val inventory = player.inventory
        for (i in 0 until inventory.containerSize) {
            val stack = inventory.getItem(i)
            if (getCharmId(stack) == id) return stack
        }

        // TODO: если чармы могут находиться в Curios/Trinkets слотах,
        // просканировать их здесь тем же способом, каким Hex Casting сканирует
        // источники медиа в MediaHelper.scanPlayerForMediaStuff.

        return null
    }
}
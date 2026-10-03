package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import miyucomics.hexical.inits.HexicalComponents
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.ItemStack

// В 1.21.1 это больше не CCItemIotaHolder, а реализация ADIotaHolder
// Регистрируется через RegisterCapabilitiesEvent для предмета HEXBURST_ITEM
class HexburstIotaHolder(val stack: ItemStack) : ADIotaHolder {
    override fun writeable(): Boolean = false

    override fun writeIota(iota: Iota?, simulate: Boolean): Boolean = false

    override fun readIota(): Iota? {
        val tag = stack.get(HexicalComponents.IOTA_HOLDER.get()) ?: return null

        // Потребляем предмет при чтении (аналог hexburst.increment(-1))
        stack.shrink(1)

        return IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, tag)
            .resultOrPartial { }.orElse(null)
    }
}
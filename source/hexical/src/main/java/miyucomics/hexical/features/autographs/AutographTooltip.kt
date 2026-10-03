package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

object AutographTooltip : InitHook() {
    override fun init() {
        // В NeoForge 1.21.1 используем ItemTooltipEvent вместо Fabric ItemTooltipCallback
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(::onTooltip)
    }

    private fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack
        val lines = event.toolTip

        // Чтение через Data Components вместо stack.nbt
        val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return
        val nbt = customData.copyTag()

        if (!nbt.contains("autographs")) return

        lines.add(Component.translatable("hexical.autograph.header")
            .withStyle(net.minecraft.ChatFormatting.GRAY))

        val list = nbt.getList("autographs", Tag.TAG_COMPOUND.toInt())
        for (element in list) {
            val compound = element as? CompoundTag ?: continue

            // В 1.21.1 FrozenPigment.fromNBT удален, используем CODEC
            val pigmentTag = compound.getCompound("pigment")
            val pigment = FrozenPigment.CODEC.parse(
                net.minecraft.nbt.NbtOps.INSTANCE,
                pigmentTag
            ).resultOrPartial { }.orElse(FrozenPigment.DEFAULT.get())

            lines.add(TextUtilities.getPigmentedText(
                compound.getString("name"),
                pigment,
                offset = ClientStorage.ticks * 3f
            ))
        }
    }
}
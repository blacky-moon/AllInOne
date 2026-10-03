package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import miyucomics.hexical.inits.HexicalComponents
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

object HexburstItem : Item(
    Properties()
        .stacksTo(16)
        .food(FoodProperties.Builder().alwaysEdible().fast().build())
) {
    // В 1.21.1 getUseDuration принимает (ItemStack, LivingEntity)
    override fun getUseDuration(stack: ItemStack, livingEntity: LivingEntity): Int = 10

    override fun finishUsingItem(stack: ItemStack, level: Level, user: LivingEntity): ItemStack {
        if (!level.isClientSide && user is ServerPlayer) {
            val iota: Iota = stack.get(HexicalComponents.IOTA_HOLDER.get())?.let { tag ->
                IotaType.TYPED_CODEC.parse(
                    level.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE),
                    tag
                ).resultOrPartial { }.orElse(null)
            } ?: GarbageIota()

            CastingUtils.giveIota(user, iota)
        }
        return super.finishUsingItem(stack, level, user)
    }

    // В 1.21.1 новая сигнатура: appendHoverText(ItemStack, TooltipContext, List<Component>, TooltipFlag)
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val tag = stack.get(HexicalComponents.IOTA_HOLDER.get()) ?: return
        val registryAccess = context.registries() ?: return

        val iota = IotaType.TYPED_CODEC.parse(
            registryAccess.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE),
            tag
        ).resultOrPartial { }.orElse(null) ?: return

        // В 1.21.1 IotaType.getDisplay(iota) больше не существует.
        // Используем iota.display(), который возвращает Component напрямую.
        val displayComponent = iota.display()

        tooltipComponents.add(
            Component.translatable("hexical.hexburst.iota", displayComponent)
                .withStyle(ChatFormatting.GRAY)
        )
    }
}
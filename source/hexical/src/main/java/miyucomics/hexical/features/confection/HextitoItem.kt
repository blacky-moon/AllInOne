package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalComponents
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

object HextitoItem : Item(
    Properties()
        .stacksTo(16)
        .food(FoodProperties.Builder().alwaysEdible().fast().build())
) {
    override fun getUseDuration(stack: ItemStack, livingEntity: LivingEntity): Int = 10

    override fun finishUsingItem(stack: ItemStack, level: Level, user: LivingEntity): ItemStack {
        if (!level.isClientSide && user is ServerPlayer) {
            val vm = CastingVM(
                IXplatAbstractions.INSTANCE.getStaffcastVM(user, InteractionHand.MAIN_HAND).image.copy(),
                HextitoCastEnv(user, InteractionHand.MAIN_HAND)
            )

            // Получаем CompoundTag из компонента HEXTITO_HEX (не IOTA_HOLDER — тот принадлежит Hexburst,
            // а гексито хранит записанный хекс отдельно, под ключом "hex" внутри wrapper-тега)
            val wrapper = stack.get(HexicalComponents.HEXTITO_HEX.get())

            if (vm.image.parenCount == 0 && wrapper != null) {
                val hex = HexSerialization.backwardsCompatibleReadHex(wrapper, "hex", level.registryAccess())

                vm.queueExecuteAndWrapIotas(hex, level as ServerLevel)
                IXplatAbstractions.INSTANCE.setStaffcastImage(user, vm.image)
            }
        }
        return super.finishUsingItem(stack, level, user)
    }
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val registryAccess = context.registries() ?: return

        val wrapper = stack.get(HexicalComponents.HEXTITO_HEX.get()) ?: return
        val hex = HexSerialization.backwardsCompatibleReadHex(wrapper, "hex", registryAccess)
        if (hex.isEmpty()) return

        val displayText = hex.fold(Component.empty()) { acc, iota ->
            acc.append(iota.display())
        }

        tooltipComponents.add(
            Component.translatable("hexical.hextito.hex", displayText)
                .withStyle(ChatFormatting.GRAY)
        )
    }
}
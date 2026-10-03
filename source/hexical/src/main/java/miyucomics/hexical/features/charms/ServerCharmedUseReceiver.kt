package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.InitHook
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object ServerCharmedUseReceiver : InitHook() {
    @JvmField
    val CHARMED_ITEM_USE_CHANNEL: ResourceLocation = HexicalMain.id("charmed_item")

    override fun init() {
        // Регистрируем обработчик на МОДОВОЙ шине, так как RegisterPayloadHandlersEvent - IModBusEvent
        HexicalMain.MOD_EVENT_BUS.addListener<RegisterPayloadHandlersEvent>(::onRegisterPayloadHandlers)
    }

    fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar: PayloadRegistrar = event.registrar(HexicalMain.MOD_ID)
        registrar.playBidirectional(
            CharmedItemUsePayload.TYPE,
            CharmedItemUsePayload.STREAM_CODEC,
            ::handle
        )
    }

    private fun handle(payload: CharmedItemUsePayload, context: IPayloadContext) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            handleServer(payload, context)
        } else {
            handleClient(payload, context)
        }
    }

    private fun handleServer(payload: CharmedItemUsePayload, context: IPayloadContext) {
        val player = context.player() as? ServerPlayer ?: return
        val inputMethod = payload.inputMethod
        val hand = payload.hand

        // Ревалидация на сервере: не доверяем клиенту, что предмет в руке
        // действительно зачарован — клиент мог отправить пакет для стека,
        // который уже не тот (задержка сети, спам пакетами и т.п.).
        val initialStack = player.getItemInHand(hand)
        if (!CharmUtilities.isStackCharmed(initialStack)) return

        val handle = CharmHandle.from(player, hand, initialStack) ?: return

        context.enqueueWork {
            // Проверяем ещё раз прямо перед стартом каста — между отправкой
            // пакета и его обработкой прошёл как минимум один тик.
            val currentStack = handle.resolveCharmed() ?: return@enqueueWork

            val level = player.serverLevel()
            val lookup = level.registryAccess()

            val vm = CastingVM(
                CastingImage().copy(stack = inputMethod.asActionResult),
                CharmCastEnv(player, hand, handle)
            )
            vm.queueExecuteAndWrapIotas(
                CharmUtilities.getHex(currentStack, lookup),
                level
            )

            // Финальный резолв — предмет мог быть перемещён (но не уничтожен)
            // самим хексом во время выполнения; postCharmCast должен получить
            // актуальный стек, а не тот, что был на момент старта.
            val finalStack = handle.resolveCharmed()
            if (finalStack != null && finalStack.item is CurioItem) {
                (finalStack.item as CurioItem).postCharmCast(
                    player,
                    finalStack,
                    hand,
                    level,
                    vm.image.stack
                )
            }
        }
    }

    private fun handleClient(payload: CharmedItemUsePayload, context: IPayloadContext) {
        // Client side handling if needed
    }

    data class CharmedItemUsePayload(
        val inputMethod: Int,
        val hand: InteractionHand
    ) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<CharmedItemUsePayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<CharmedItemUsePayload>(HexicalMain.id("charmed_item"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CharmedItemUsePayload> = StreamCodec.composite(
                ByteBufCodecs.INT,
                CharmedItemUsePayload::inputMethod,
                ByteBufCodecs.INT.map(
                    { InteractionHand.entries[it] },
                    { it.ordinal }
                ),
                CharmedItemUsePayload::hand,
                ::CharmedItemUsePayload
            )
        }
    }
}
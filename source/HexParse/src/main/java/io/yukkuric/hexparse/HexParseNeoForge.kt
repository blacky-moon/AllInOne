package io.yukkuric.hexparse

import at.petrak.hexcasting.common.lib.HexRegistries
import io.yukkuric.hexparse.actions.HexParsePatterns
import io.yukkuric.hexparse.config.HexParseConfigForge
import io.yukkuric.hexparse.events.MacroForgeHandler
import io.yukkuric.hexparse.hooks.CommentIotaType
import io.yukkuric.hexparse.hooks.HexParseCommands
import io.yukkuric.hexparse.network.*
import io.yukkuric.hexparse.network.macro.MsgPushMacro
import io.yukkuric.hexparse.network.macro.MsgUpdateClientMacro
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper.doCollect
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper.sendRemoteMap
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.handling.IPayloadHandler
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.function.BiConsumer
import java.util.function.Consumer

@Mod(HexParse.MOD_ID)
class HexParseNeoForge(modBus: IEventBus, modContainer: ModContainer?) {
    init {
        val evBus = NeoForge.EVENT_BUS

        NETWORK = Network(modBus)
        HELPERS = ModHelpers()

        // Run our common setup.
        HexParse.init()

        evBus.addListener<RegisterCommandsEvent?>(Consumer { event: RegisterCommandsEvent? ->
            HexParseCommands.register(
                event!!.getDispatcher()
            )
        })
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, { { evBus.register(MacroForgeHandler::class.java) } })
        evBus.addListener<ServerStartedEvent?>(Consumer { e: ServerStartedEvent? ->
            doCollect()
        })
        evBus.addListener<PlayerLoggedInEvent?>(Consumer { e: PlayerLoggedInEvent? ->
            val sp = e!!.getEntity()
            if (sp is ServerPlayer) sendRemoteMap(sp)
        })

        modBus.addListener<RegisterEvent?>(Consumer { event: RegisterEvent? ->
            val key = event!!.getRegistryKey()
            if (key == HexRegistries.ACTION) {
                HexParsePatterns.registerActions()
            } else if (key == HexRegistries.IOTA_TYPE) CommentIotaType.registerIota()
        })

        HexParseConfigForge.register(modContainer)


        // init client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, { { HexParse.initClient() } })
    }

    class Network internal constructor(modBus: IEventBus) : ISenderClient, ISenderServer {
        init {
            MsgHandlers.CLIENT = this
            MsgHandlers.SERVER = this

            modBus.addListener<RegisterPayloadHandlersEvent?>(
                RegisterPayloadHandlersEvent::class.java,
                Consumer { e: RegisterPayloadHandlersEvent? ->
                    val reg = e!!.registrar(PROTOCOL_VERSION)
                    // s2c
                    reg.playToClient<MsgPullClipboard?>(
                        MsgPullClipboard.TYPE, MsgPullClipboard.STREAM_CODEC, makeClientBoundHandler<MsgPullClipboard?>(
                            Consumer { self: MsgPullClipboard? -> MsgPullClipboard.handle(self) })
                    )
                    reg.playToClient<MsgUpdateClientMacro?>(
                        MsgUpdateClientMacro.TYPE,
                        MsgUpdateClientMacro.STREAM_CODEC,
                        makeClientBoundHandler<MsgUpdateClientMacro?>(
                            Consumer { self: MsgUpdateClientMacro? -> MsgUpdateClientMacro.handle(self) })
                    )
                    reg.playToClient<MsgSyncDisplayMap?>(
                        MsgSyncDisplayMap.TYPE,
                        MsgSyncDisplayMap.STREAM_CODEC,
                        makeClientBoundHandler<MsgSyncDisplayMap?>(
                            Consumer { self: MsgSyncDisplayMap? -> MsgSyncDisplayMap.handle(self) })
                    )
                    // c2s
                    reg.playToServer<MsgPushClipboard?>(
                        MsgPushClipboard.TYPE, MsgPushClipboard.STREAM_CODEC, makeServerBoundHandler<MsgPushClipboard?>(
                            BiConsumer { self: MsgPushClipboard?, sender: ServerPlayer? ->
                                MsgPushClipboard.handle(
                                    self,
                                    sender
                                )
                            })
                    )
                    reg.playToServer<MsgPushMacro?>(
                        MsgPushMacro.TYPE, MsgPushMacro.STREAM_CODEC, makeServerBoundHandler<MsgPushMacro?>(
                            BiConsumer { self: MsgPushMacro?, sender: ServerPlayer? ->
                                MsgPushMacro.handle(
                                    self,
                                    sender
                                )
                            })
                    )
                })
        }

        override fun sendPacketToServer(packet: CustomPacketPayload) {
            PacketDistributor.sendToServer(packet)
        }

        override fun sendPacketToPlayer(player: ServerPlayer, packet: CustomPacketPayload) {
            PacketDistributor.sendToPlayer(player, packet)
        }

        companion object {
            const val PROTOCOL_VERSION: String = "1"

            fun <T : CustomPacketPayload?> makeServerBoundHandler(handler: BiConsumer<T?, ServerPlayer?>): IPayloadHandler<T?> {
                return IPayloadHandler { m: T?, ctx: IPayloadContext? ->
                    handler.accept(m, ctx!!.player() as ServerPlayer)
                }
            }

            fun <T : CustomPacketPayload?> makeClientBoundHandler(consumer: Consumer<T?>): IPayloadHandler<T?> {
                return IPayloadHandler { m: T?, ctx: IPayloadContext? ->
                    consumer.accept(m)
                }
            }
        }
    }

    class ModHelpers internal constructor() : IModHelpers {
        private var isClient = false

        init {
            HexParse.HELPERS = this
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, { { isClient = true } })
        }

        override fun modLoaded(modId: String?): Boolean {
            return ModList.get().isLoaded(modId)
        }

        override fun isPhysicalClient(): Boolean {
            return isClient
        }
    }

    companion object {
        var NETWORK: Network? = null
        var HELPERS: ModHelpers? = null
    }
}
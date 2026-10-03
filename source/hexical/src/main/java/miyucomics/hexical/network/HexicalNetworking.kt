package miyucomics.hexical.network

import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.confetti.ConfettiPayload
import miyucomics.hexical.features.evocation.EndEvokePayload
import miyucomics.hexical.features.evocation.StartEvokePayload
import miyucomics.hexical.features.evocation.evocationActive
import miyucomics.hexical.features.lesser_sentinels.LesserSentinelPayload
import miyucomics.hexical.features.media_log.MediaLogField
import miyucomics.hexical.features.media_log.MediaLogPayload
import miyucomics.hexical.features.shaders.ShaderPayload
import miyucomics.hexical.features.shaders.ShaderRenderer
import miyucomics.hexical.features.toast.HexToast
import miyucomics.hexical.features.toast.ToastPayload
import miyucomics.hexical.inits.HexicalParticles
import net.minecraft.resources.ResourceLocation
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@EventBusSubscriber(modid = HexicalMain.MOD_ID)
object HexicalNetworking {
    @JvmStatic
    @SubscribeEvent
    fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        val registrar: PayloadRegistrar = event.registrar(HexicalMain.MOD_ID)

        // Server-bound


        // Client-bound: must be registered on both sides so a dedicated server recognises them.
        // Actual handling only happens on the client; the server handler is empty.


        registrar.playToClient(ToastPayload.TYPE, ToastPayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                val client = Minecraft.getInstance()
                client.toasts.addToast(HexToast(payload.title, payload.description, payload.stack))
            }
        }

        registrar.playToClient(ConfettiPayload.TYPE, ConfettiPayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                val client = Minecraft.getInstance()
                val level = client.level ?: return@runIfClient
                val random = Random(payload.seed)
                val pos = payload.pos
                val dir = payload.dir
                val speed = payload.speed

                level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.MASTER, 1f, 1f, false)

                for (i in 0..99) {
                    val alteredVelocity = if (dir == Vec3.ZERO) {
                        val pitchRad = Mth.DEG_TO_RAD * (random.nextFloat() * 180f - 90f)
                        val yawRad = Mth.DEG_TO_RAD * (random.nextFloat() * 360f)
                        val cosPitch = cos(pitchRad.toDouble())
                        Vec3(
                            sin(yawRad.toDouble()) * cosPitch,
                            sin(pitchRad.toDouble()),
                            cos(yawRad.toDouble()) * cosPitch
                        ).scale(speed)
                    } else {
                        dir.add(
                            (random.nextDouble() * 2 - 1) / 5,
                            (random.nextDouble() * 2 - 1) / 5,
                            (random.nextDouble() * 2 - 1) / 5
                        ).scale((random.nextFloat() * 0.25 + 0.75) * speed)
                    }

                    level.addParticle(
                        HexicalParticles.CONFETTI_PARTICLE.get(),
                        pos.x, pos.y, pos.z,
                        alteredVelocity.x, alteredVelocity.y, alteredVelocity.z
                    )
                }
            }
        }

        registrar.playToClient(ShaderPayload.TYPE, ShaderPayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                val location = if (payload.shaderId == "null") null else ResourceLocation.parse(payload.shaderId)
                ShaderRenderer.setEffect(location)
            }
        }

        registrar.playToClient(LesserSentinelPayload.TYPE, LesserSentinelPayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                ClientStorage.lesserSentinels.clear()
                ClientStorage.lesserSentinels.addAll(payload.positions)
            }
        }

        registrar.playToClient(MediaLogPayload.TYPE, MediaLogPayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                val registries = Minecraft.getInstance().level?.registryAccess() ?: return@runIfClient
                ClientStorage.mediaLog = MediaLogField().also { it.fromNbt(payload.data, registries) }
            }
        }



        registrar.playToClient(StartEvokePayload.TYPE, StartEvokePayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                val client = Minecraft.getInstance()
                val player = client.level?.getPlayerByUUID(payload.playerUUID) ?: return@runIfClient
                player.evocationActive = true
            }
        }

        registrar.playToClient(EndEvokePayload.TYPE, EndEvokePayload.STREAM_CODEC) { payload, context ->
            runIfClient {
                val client = Minecraft.getInstance()
                val player = client.level?.getPlayerByUUID(payload.playerUUID) ?: return@runIfClient
                player.evocationActive = false
            }
        }
    }

    private inline fun runIfClient(block: () -> Unit) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            block()
        }
    }
}

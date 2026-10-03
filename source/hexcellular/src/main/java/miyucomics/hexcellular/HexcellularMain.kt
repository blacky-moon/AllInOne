package miyucomics.hexcellular

import at.petrak.hexcasting.common.lib.HexRegistries
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.RegisterEvent

@Mod(HexcellularMain.MOD_ID)
class HexcellularMain(modBus: IEventBus) {
    init {
        modBus.addListener(::registerIotaTypes)
        modBus.addListener(::registerActions)
        NeoForge.EVENT_BUS.addListener(::registerCommands)
    }

    private fun registerIotaTypes(event: RegisterEvent) {
        event.register(HexRegistries.IOTA_TYPE) { helper ->
            helper.register(id("property"), PropertyIota.TYPE)
        }
    }
    private fun registerActions(event: RegisterEvent) {
        if (event.registryKey == HexRegistries.ACTION) {
            HexcellularActions.init()
        }
    }
    private fun registerCommands(event: RegisterCommandsEvent) {
        val dispatcher = event.dispatcher
        dispatcher.register(
            Commands.literal("hexcellular")
                .requires { source -> source.hasPermission(2) }
                .then(createPropertySubcommand("giveProperty") { context, level, property ->
                    val player = context.source.playerOrException
                    val stack = net.minecraft.world.item.ItemStack(at.petrak.hexcasting.common.lib.HexItems.THOUGHT_KNOT)
                    val holder = at.petrak.hexcasting.xplat.IXplatAbstractions.INSTANCE.findDataHolder(stack)
                    holder?.writeIota(PropertyIota(property, false), false)
                    player.inventory.add(stack)
                    context.source.sendSuccess({ Component.translatable("hexcellular.property.given", property, player.name) }, false)
                    if (!StateStorage.listProperties(level).contains(property))
                        StateStorage.setProperty(level, property, at.petrak.hexcasting.api.casting.iota.NullIota())
                    1
                })
                .then(createPropertySubcommand("removeProperty") { context, level, property ->
                    StateStorage.removeProperty(level, property)
                    context.source.sendSuccess({ Component.translatable("hexcellular.property.removed", property) }, false)
                    1
                })
        )
    }

    private fun createPropertySubcommand(
        name: String,
        method: (CommandContext<CommandSourceStack>, ServerLevel, String) -> Int
    ): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal(name)
            .then(
                Commands.argument("property", StringArgumentType.word())
                    .suggests(SuggestionProvider { context, builder ->
                        StateStorage.listProperties(context.source.level)
                            .filter { SharedSuggestionProvider.matchesSubStr(builder.remaining, it) }
                            .forEach(builder::suggest)
                        builder.buildFuture()
                    })
                    .executes { method(it, it.source.level, StringArgumentType.getString(it, "property")) }
            )
    }

    companion object {
        const val MOD_ID: String = "hexcellular"
        fun id(string: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, string)
    }
}
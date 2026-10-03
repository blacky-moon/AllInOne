package miyucomics.hexical.features.player

import miyucomics.hexical.HexicalMain
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.attachment.IAttachmentHolder
import net.neoforged.neoforge.attachment.IAttachmentSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object HexicalAttachments {
    val ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HexicalMain.MOD_ID)

    val HEXICAL_PLAYER_MANAGER = ATTACHMENT_TYPES.register(
        "hexical_player_manager",
        Supplier<AttachmentType<PlayerManager>> {
            AttachmentType.builder(::PlayerManager)
                .serialize(object : IAttachmentSerializer<CompoundTag, PlayerManager> {
                    override fun read(holder: IAttachmentHolder, tag: CompoundTag, registries: HolderLookup.Provider): PlayerManager {
                        val manager = PlayerManager()
                        manager.readNbt(tag, registries)
                        return manager
                    }

                    override fun write(data: PlayerManager, registries: HolderLookup.Provider): CompoundTag {
                        val tag = CompoundTag()
                        data.writeNbt(tag, registries)
                        return tag
                    }
                })
                .copyOnDeath()
                .build()
        }
    )

    fun init(modEventBus: net.neoforged.bus.api.IEventBus) {
        ATTACHMENT_TYPES.register(modEventBus)
    }
}
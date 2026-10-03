package miyucomics.hexical.datagen.generators

import at.petrak.hexcasting.api.mod.HexTags
import miyucomics.hexical.inits.*
import net.minecraft.advancements.*
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.data.AdvancementProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class HexicalAdvancementGenerator(
    private val registries: CompletableFuture<HolderLookup.Provider>
) : AdvancementProvider.AdvancementGenerator {

    override fun generate(registries: HolderLookup.Provider, saver: Consumer<AdvancementHolder>, filehandler: ExistingFileHelper) {
        val rootId = ResourceLocation.fromNamespaceAndPath("hexical", "root")

        // Корневое достижение
        Advancement.Builder.advancement()
            .display(
                ItemStack(HexicalItems.CURIO_COMPASS.get()),
                Component.translatable("advancement.hexical:root.title"),
                Component.translatable("advancement.hexical:root.description"),
                ResourceLocation.withDefaultNamespace("textures/block/blackstone.png"),
                AdvancementType.TASK,
                true, true, true
            )
            .addCriterion("start_hexcasting",
                InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(HexTags.Items.GRANTS_ROOT_ADVANCEMENT).build()
                )
            )
            .save(saver, rootId, filehandler)

        fun registerAdvancement(
            name: String,
            icon: Item,
            criterionName: String,
            criterion: Criterion<*>,
            frame: AdvancementType = AdvancementType.TASK,
            hidden: Boolean = false,
            parent: AdvancementHolder? = null
        ): ResourceLocation {
            val advId = ResourceLocation.fromNamespaceAndPath("hexical", name)
            val builder = Advancement.Builder.advancement()

            if (parent != null) {
                builder.parent(parent)
            }

            builder.display(
                ItemStack(icon),
                Component.translatable("advancements.hexical.$name.title"),
                Component.translatable("advancements.hexical.$name.description"),
                null,
                frame,
                true, true, hidden
            )
                .addCriterion(criterionName, criterion)
                .save(saver, advId, filehandler)
            return advId
        }

        // Используем createCriterion() для создания Criterion
        registerAdvancement("conjure_cake", Items.CAKE, "conjure_cake",
            HexicalAdvancements.CONJURE_CAKE.createCriterion(ConjureCakeCriterion.Condition()),
            frame = AdvancementType.CHALLENGE,
            hidden = true
        )

        registerAdvancement("diy_conjuring", Items.SCAFFOLDING, "diy_conjuring",
            HexicalAdvancements.DIY.createCriterion(DIYCriterion.Condition())
        )

        registerAdvancement("hextito_quine", HexicalItems.HEXTITO_ITEM.get(), "hextito_quine",
            HexicalAdvancements.HEXTITO_QUINE.createCriterion(HextitoQuineCriterion.Condition())
        )

        registerAdvancement("specklike", Items.BEACON, "specklike",
            HexicalAdvancements.AR.createCriterion(SpecklikeCriterion.Condition())
        )

        val handLamp = HexicalItems.HAND_LAMP_ITEM.get()
        val archLamp = HexicalItems.ARCH_LAMP_ITEM.get()

        // Сохраняем родительское достижение
        val acquireHandLampId = ResourceLocation.fromNamespaceAndPath("hexical", "acquire_hand_lamp")
        val acquireHandLampHolder = Advancement.Builder.advancement()
            .display(
                ItemStack(handLamp),
                Component.translatable("advancements.hexical.acquire_hand_lamp.title"),
                Component.translatable("advancements.hexical.acquire_hand_lamp.description"),
                null,
                AdvancementType.TASK,
                true, true, false
            )
            .addCriterion("acquire_hand_lamp", InventoryChangeTrigger.TriggerInstance.hasItems(handLamp))
            .save(saver, acquireHandLampId, filehandler)

        registerAdvancement("educate_lamp", Items.ENCHANTED_BOOK, "educate_lamp",
            HexicalAdvancements.EDUCATE_GENIE.createCriterion(EducateGenieCriterion.Condition()),
            parent = acquireHandLampHolder
        )

        registerAdvancement("acquire_arch_lamp", archLamp, "acquire_arch_lamp",
            InventoryChangeTrigger.TriggerInstance.hasItems(archLamp),
            frame = AdvancementType.GOAL,
            parent = acquireHandLampHolder
        )
    }
}
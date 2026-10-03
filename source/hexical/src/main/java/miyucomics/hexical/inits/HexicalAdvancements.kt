package miyucomics.hexical.inits

import com.mojang.serialization.Codec
import miyucomics.hexical.HexicalMain
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.Optional

object HexicalAdvancements {
    lateinit var AR: SpecklikeCriterion
    lateinit var CONJURE_CAKE: ConjureCakeCriterion
    lateinit var DIY: DIYCriterion
    lateinit var EDUCATE_GENIE: EducateGenieCriterion
    lateinit var HEXTITO_QUINE: HextitoQuineCriterion

    val EVOCATION_STATISTIC: ResourceLocation = HexicalMain.id("evocation")

    fun registerAll(helper: RegisterEvent.RegisterHelper<CriterionTrigger<*>>) {
        AR = register(helper, HexicalMain.id("specklike"), SpecklikeCriterion())
        CONJURE_CAKE = register(helper, HexicalMain.id("conjure_cake"), ConjureCakeCriterion())
        DIY = register(helper, HexicalMain.id("diy_conjuring"), DIYCriterion())
        EDUCATE_GENIE = register(helper, HexicalMain.id("educate_genie"), EducateGenieCriterion())
        HEXTITO_QUINE = register(helper, HexicalMain.id("hextito_quine"), HextitoQuineCriterion())
    }

    private fun <T : SimpleCriterionTrigger<*>> register(
        helper: RegisterEvent.RegisterHelper<CriterionTrigger<*>>,
        id: ResourceLocation,
        trigger: T
    ): T {
        helper.register(id, trigger)
        return trigger
    }
}

// === Классы триггеров (без изменений) ===

abstract class BaseCriterion<T : BaseCondition> : SimpleCriterionTrigger<T>() {
    fun trigger(player: ServerPlayer) = trigger(player) { true }

    override fun createCriterion(condition: T): Criterion<T> {
        return Criterion(this, condition)
    }
}

abstract class BaseCondition : SimpleCriterionTrigger.SimpleInstance {
    override fun player(): Optional<ContextAwarePredicate> = Optional.empty()
}

class ConjureCakeCriterion : BaseCriterion<ConjureCakeCriterion.Condition>() {
    override fun codec(): Codec<Condition> = CODEC
    class Condition : BaseCondition()
    companion object {
        val CODEC: Codec<Condition> = Codec.unit(::Condition)
    }
}

class DIYCriterion : BaseCriterion<DIYCriterion.Condition>() {
    override fun codec(): Codec<Condition> = CODEC
    class Condition : BaseCondition()
    companion object {
        val CODEC: Codec<Condition> = Codec.unit(::Condition)
    }
}

class EducateGenieCriterion : BaseCriterion<EducateGenieCriterion.Condition>() {
    override fun codec(): Codec<Condition> = CODEC
    class Condition : BaseCondition()
    companion object {
        val CODEC: Codec<Condition> = Codec.unit(::Condition)
    }
}

class HextitoQuineCriterion : BaseCriterion<HextitoQuineCriterion.Condition>() {
    override fun codec(): Codec<Condition> = CODEC
    class Condition : BaseCondition()
    companion object {
        val CODEC: Codec<Condition> = Codec.unit(::Condition)
    }
}

class SpecklikeCriterion : BaseCriterion<SpecklikeCriterion.Condition>() {
    override fun codec(): Codec<Condition> = CODEC
    class Condition : BaseCondition()
    companion object {
        val CODEC: Codec<Condition> = Codec.unit(::Condition)
    }
}
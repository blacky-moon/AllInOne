package miyucomics.hexical.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.vecFromNBT
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.akashic_shelves.*
import miyucomics.hexical.features.amber_seal.OpAmberSeal
import miyucomics.hexical.features.autographs.*
import miyucomics.hexical.features.block_mimicry.*
import miyucomics.hexical.features.breaking.*
import miyucomics.hexical.features.charms.*
import miyucomics.hexical.features.circle.*
import miyucomics.hexical.features.confection.*
import miyucomics.hexical.features.confetti.OpConfetti
import miyucomics.hexical.features.conjure.*
import miyucomics.hexical.features.dda.OpDda
import miyucomics.hexical.features.driver_dots.OpProgramDriver
import miyucomics.hexical.features.dyes.actions.*
import miyucomics.hexical.features.evocation.*
import miyucomics.hexical.features.flora.OpConjureFlora
import miyucomics.hexical.features.grimoires.*
import miyucomics.hexical.features.grok.*
import miyucomics.hexical.features.jailbreak.OpJailbreakDevice
import miyucomics.hexical.features.lamps.*
import miyucomics.hexical.features.lesser_sentinels.*
import miyucomics.hexical.features.mage_blocks.*
import miyucomics.hexical.features.mage_blocks.modifiers.*
import miyucomics.hexical.features.mage_hand.OpMageHand
import miyucomics.hexical.features.mage_mouth.OpMageMouth
import miyucomics.hexical.features.magic_missile.OpMagicMissile
import miyucomics.hexical.features.misc_actions.*
import miyucomics.hexical.features.mute.*
import miyucomics.hexical.features.pattern_manipulation.*
import miyucomics.hexical.features.periwinkle.OpCompelSniffer
import miyucomics.hexical.features.personal_inventory.*
import miyucomics.hexical.features.pigments.*
import miyucomics.hexical.features.prestidigitation.OpPrestidigitation
import miyucomics.hexical.features.rotate.*
import miyucomics.hexical.features.sentinel_defense.*
import miyucomics.hexical.features.shaders.OpShader
import miyucomics.hexical.features.sparkle.OpSparkle
import miyucomics.hexical.features.specklikes.actions.*
import miyucomics.hexical.features.specklikes.mesh.*
import miyucomics.hexical.features.specklikes.speck.*
import miyucomics.hexical.features.specklikes.strand.*
import miyucomics.hexical.features.spike.OpConjureSpike
import miyucomics.hexical.features.telepathy.*
import miyucomics.hexical.features.toast.*
import miyucomics.hexical.features.wristpocket.*
import miyucomics.hexical.features.zap.OpZap
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.*
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalActions {
    fun registerAll(helper: RegisterEvent.RegisterHelper<ActionRegistryEntry>) {
        registerAction(helper, "program_driver", "aqqqqaw", HexDir.NORTH_WEST, OpProgramDriver)

        registerAction(helper, "write_grimoire", "aqwqaeaqa", HexDir.WEST, OpGrimoireWrite)
        registerAction(helper, "erase_grimoire", "aqwqaqded", HexDir.WEST, OpGrimoireErase)
        registerAction(helper, "index_grimoire", "aqaeaqwqa", HexDir.SOUTH_EAST, OpGrimoireIndex)

        registerAction(helper, "periwinkle", "wwwaqdadaadadqqqeaeq", HexDir.EAST, OpCompelSniffer)

        registerAction(helper, "get_lamp_position", "qaqwddedqdd", HexDir.NORTH_EAST, OpGetLampData { _, nbt -> vecFromNBT(nbt.getCompound("position")).asActionResult })
        registerAction(helper, "get_lamp_rotation", "qaqwddedadw", HexDir.NORTH_EAST, OpGetLampData { _, nbt -> vecFromNBT(nbt.getCompound("rotation")).asActionResult })
        registerAction(helper, "get_lamp_velocity", "qaqwddedqew", HexDir.NORTH_EAST, OpGetLampData { _, nbt -> vecFromNBT(nbt.getCompound("velocity")).asActionResult })
        registerAction(helper, "get_lamp_use_time", "qaqwddedqwddwa", HexDir.NORTH_EAST, OpGetLampData { env, nbt -> (env.world.gameTime - (nbt.getDouble("start_time") + 1.0)).asActionResult })
        registerAction(helper, "get_lamp_media", "qaqwddedaeeeee", HexDir.NORTH_EAST, OpGetLampData { env, _ -> ((env.castingEntity!!.useItem.item as HandLampItem).getMedia(env.castingEntity!!.useItem).toDouble() / MediaConstants.DUST_UNIT).asActionResult })
        registerAction(helper, "get_lamp_storage", "qaqwddedqwaqqqqq", HexDir.NORTH_EAST, OpGetLampData { env, nbt ->
            val storageTag = nbt.get("storage")
            val iota = if (storageTag != null)
                IotaType.TYPED_CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, env.world.registryAccess()), storageTag).result().orElse(NullIota())
            else NullIota()
            listOf(iota)
        })
        registerAction(helper, "wish", "eweweweweweewedeaqqqd", HexDir.NORTH_WEST, OpWish)
        registerAction(helper, "set_lamp_storage", "qaqwddedqedeeeee", HexDir.NORTH_EAST, OpSetLampStorage)
        registerAction(helper, "lamp_finale", "aaddaddad", HexDir.EAST, OpGetFinale)

        registerAction(helper, "rotate_block", "edeeeeeweewadeeed", HexDir.EAST, OpRotateBlock)
        registerAction(helper, "rotate_entity", "qqqdaqqqa", HexDir.EAST, OpRotateEntity)

        registerAction(helper, "shuffle_pattern", "aqqqdae", HexDir.NORTH_EAST, OpShufflePattern)
        registerAction(helper, "congruent", "aaqd", HexDir.EAST, OpCongruentPattern)
        registerAction(helper, "similar", "aedd", HexDir.EAST, OpSimilarPattern)
        registerAction(helper, "serialize_pattern", "wqaedeqd", HexDir.EAST, OpDisintegratePattern)
        registerAction(helper, "deserialize_pattern", "wqqqaqwd", HexDir.EAST, OpIntegratePattern)

        registerAction(helper, "grok_get_stack", "aqawwqaw", HexDir.EAST, OpGrokGetStack)
        registerAction(helper, "grok_set_stack", "ewdewwde", HexDir.EAST, OpGrokSetStack)
        registerAction(helper, "grok_get_parenthesized", "waqwawwqwaw", HexDir.EAST, OpGrokGetParenthesized)
        registerAction(helper, "grok_set_parenthesized", "wewdwewwdwe", HexDir.EAST, OpGrokSetParenthesized)

        registerAction(helper, "dda", "wdqqdwewdqqdwdqdadedaddww", HexDir.NORTH_EAST, OpDda)

        registerAction(helper, "get_telepathy", "wqqadaw", HexDir.EAST, OpGetKeybindSelf("key.hexical.telepathy"))
        registerAction(helper, "left_click", "qadee", HexDir.NORTH_EAST, OpGetKeybindSelf("key.attack"))
        registerAction(helper, "right_click", "edaqq", HexDir.NORTH_WEST, OpGetKeybindSelf("key.use"))
        registerAction(helper, "moving_up", "aqaddq", HexDir.SOUTH_EAST, OpGetKeybindSelf("key.forward"))
        registerAction(helper, "moving_down", "dedwdq", HexDir.SOUTH_WEST, OpGetKeybindSelf("key.back"))
        registerAction(helper, "moving_left", "edead", HexDir.SOUTH_EAST, OpGetKeybindSelf("key.left"))
        registerAction(helper, "moving_right", "qaqda", HexDir.SOUTH_WEST, OpGetKeybindSelf("key.right"))
        registerAction(helper, "sneaking", "wede", HexDir.NORTH_WEST, OpGetKeybindSelf("key.sneak"))
        registerAction(helper, "sprinting", "wqaq", HexDir.NORTH_WEST, OpGetKeybindSelf("key.sprint"))
        registerAction(helper, "jumping", "qaqdaqqa", HexDir.SOUTH_WEST, OpGetKeybindSelf("key.jump"))
        registerAction(helper, "scroll", "qadeeee", HexDir.NORTH_EAST, OpGetScroll)
        registerAction(helper, "left_click_other", "wqadee", HexDir.NORTH_EAST, OpGetKeybindOther("key.attack"))
        registerAction(helper, "right_click_other", "wedaqq", HexDir.NORTH_WEST, OpGetKeybindOther("key.use"))
        registerAction(helper, "moving_up_other", "aqaddqw", HexDir.SOUTH_EAST, OpGetKeybindOther("key.forward"))
        registerAction(helper, "moving_down_other", "dedwdqw", HexDir.SOUTH_WEST, OpGetKeybindOther("key.back"))
        registerAction(helper, "moving_left_other", "edeadw", HexDir.SOUTH_EAST, OpGetKeybindOther("key.left"))
        registerAction(helper, "moving_right_other", "qaqdaw", HexDir.SOUTH_WEST, OpGetKeybindOther("key.right"))
        registerAction(helper, "jumping_other", "qaqdawawa", HexDir.SOUTH_WEST, OpGetKeybindOther("key.jump"))
        registerAction(helper, "sneaking_other", "wwede", HexDir.NORTH_WEST, OpGetKeybindOther("key.sneak"))

        registerAction(helper, "toast", "ewqqqwqqaee", HexDir.NORTH_EAST, OpToast)
        registerAction(helper, "send_telepathy", "qqqqwaqa", HexDir.EAST, OpSendTelepathy)
        registerAction(helper, "shout_telepathy", "daqqqqwa", HexDir.EAST, OpShoutTelepathy)

        registerAction(helper, "key_shelf", "qaqqadaq", HexDir.EAST, OpKeyAkashicShelf)
        registerAction(helper, "read_shelf", "qaqqqada", HexDir.EAST, OpReadAkashicShelf)
        registerAction(helper, "write_shelf", "edeeedad", HexDir.SOUTH_WEST, OpWriteAkashicShelf)
        registerAction(helper, "clear_shelf", "edeedade", HexDir.SOUTH_WEST, OpClearAkashicShelf)

        registerAction(helper, "conjure_mage_block", "dee", HexDir.NORTH_WEST, OpConjureMageBlock)
        registerAction(helper, "reset_mage_block", "deeeaw", HexDir.NORTH_WEST, OpResetMageBlock)
        registerAction(helper, "modify_block_bouncy", "deeqa", HexDir.NORTH_WEST, OpConfigureMageBlock(BouncyModifier.TYPE))
        registerAction(helper, "modify_block_ephemeral", "deewwaawd", HexDir.NORTH_WEST, OpConfigureMageBlock(LifespanModifier.TYPE))
        registerAction(helper, "modify_block_energized", "deewad", HexDir.NORTH_WEST, OpConfigureMageBlock(RedstoneModifier.TYPE))
        registerAction(helper, "modify_block_volatile", "deewedeeeee", HexDir.NORTH_WEST, OpConfigureMageBlock(VolatileModifier.TYPE))

        registerAction(helper, "autograph", "eeeeeww", HexDir.WEST, OpAutograph)
        registerAction(helper, "unautograph", "wwqqqqq", HexDir.NORTH_EAST, OpUnautograph)
        registerAction(helper, "has_autograph", "wwqqqqqaw", HexDir.NORTH_EAST, OpHasAutograph)

        registerAction(helper, "get_dye", "weedwa", HexDir.NORTH_EAST, OpGetDye)
        registerAction(helper, "dye", "dwaqqw", HexDir.NORTH_WEST, OpDye)
        registerAction(helper, "translate_dye", "wdwwaawwewdwwewwdwwe", HexDir.EAST, OpTranslateDye)

        registerAction(helper, "magic_missile", "qaqww", HexDir.WEST, OpMagicMissile)

        registerAction(helper, "to_pigment", "aqwedeweeeewweeew", HexDir.NORTH_WEST, OpToPigment)
        registerAction(helper, "sample_pigment", "edewqaqqqqqwqqq", HexDir.SOUTH_EAST, OpSamplePigment)
        registerAction(helper, "take_on_pigment", "weeeweeqeeeewqaqweeee", HexDir.EAST, OpTakeOnPigment)

        registerAction(helper, "amber_seal", "qaqeaqwqwqa", HexDir.EAST, OpAmberSeal)

        registerAction(helper, "mage_hand", "dqq", HexDir.NORTH_WEST, OpMageHand)

        registerAction(helper, "mage_mouth", "eeede", HexDir.EAST, OpMageMouth)

        registerAction(helper, "prestidigitation", "wedewedew", HexDir.NORTH_EAST, OpPrestidigitation)

        registerAction(helper, "wristpocket", "aaqqa", HexDir.WEST, OpWristpocket)
        registerAction(helper, "wristpocket_item", "aaqqada", HexDir.WEST, OpGetWristpocket)
        registerAction(helper, "sleight", "aaqqaded", HexDir.WEST, OpSleight)

        registerAction(helper, "conjure_flora", "weqqqqqwaeaeaeaeaea", HexDir.NORTH_EAST, OpConjureFlora)

        registerAction(helper, "confetti", "awddeqaedd", HexDir.EAST, OpConfetti)
        registerAction(helper, "vibration", "wwawawwd", HexDir.EAST, OpVibrate)
        registerAction(helper, "sparkle", "dqa", HexDir.NORTH_EAST, OpSparkle)
        registerAction(helper, "jailbreak", "wwaqqqqqeqdedwqeaeqwdedwqeaeq", HexDir.EAST, OpJailbreakDevice)
        registerAction(helper, "light", "aeaeaeaeaeawqqqqq", HexDir.SOUTH_EAST, OpConjureLight)
        registerAction(helper, "gasp", "aweeeeewaweeeee", HexDir.NORTH_WEST, OpGasp)
        registerAction(helper, "parrot", "wweedadw", HexDir.NORTH_EAST, OpImitateParrot)

        registerAction(helper, "egg", "qqqwaqaaqeeewdedde", HexDir.SOUTH_EAST, OpConjureEntity(MediaConstants.DUST_UNIT * 2) { world, position, caster -> ThrownEgg(world, position.x, position.y, position.z).apply { owner = caster } })
        registerAction(helper, "llama_spit", "dwqaqw", HexDir.EAST, OpConjureEntity(MediaConstants.DUST_UNIT / 4) { world, position, caster -> LlamaSpit(EntityType.LLAMA_SPIT, world).apply {
            setPos(position)
            owner = caster
        }})
        registerAction(helper, "snowball", "ddeeeeewd", HexDir.NORTH_EAST, OpConjureEntity(MediaConstants.DUST_UNIT / 2) { world, position, caster -> Snowball(world, position.x, position.y, position.z).apply {
            owner = caster
        }})
        registerAction(helper, "ghast_fireball", "wqqqqqwaeaeaeaeae", HexDir.SOUTH_EAST, OpConjureEntity(MediaConstants.DUST_UNIT * 3) { world, position, caster -> LargeFireball(world, caster!!, Vec3.ZERO, 1).apply {
            setPos(position)
        }})

        registerAction(helper, "mute", "wddaq", HexDir.EAST, OpMute)
        registerAction(helper, "is_mute", "edaaw", HexDir.NORTH_WEST, OpIsMute)

        registerAction(helper, "break_fortune", "qaqqqqqdeeeqeee", HexDir.EAST, OpBreakFortune)
        registerAction(helper, "break_silk", "aqaeaqdeeweweedq", HexDir.EAST, OpBreakSilk)

        registerAction(helper, "zap", "qad", HexDir.WEST, OpZap)

        registerAction(helper, "conjure_gummy", "eeewdw", HexDir.SOUTH_WEST, OpConjureGummy)
        registerAction(helper, "conjure_hexburst", "aadaadqaq", HexDir.EAST, OpConjureHexburst)
        registerAction(helper, "conjure_hextito", "qaqdqaqdwawaw", HexDir.EAST, OpConjureHextito)

        registerAction(helper, "spike", "qdqdqdqdww", HexDir.NORTH_EAST, OpConjureSpike)

        registerAction(helper, "piston", "wqwawqwqqqeqq", HexDir.SOUTH_WEST, OpPiston)
        registerAction(helper, "dispense", "wqwawqwddaeeead", HexDir.SOUTH_WEST, OpDispense)
        registerAction(helper, "smelt", "qwqqadadadewewewe", HexDir.SOUTH_EAST, OpCook(RecipeType.SMELTING, "target.smelting"))
        registerAction(helper, "roast", "aqqwwqqawdadedad", HexDir.NORTH_WEST, OpCook(RecipeType.CAMPFIRE_COOKING, "target.roasting"))
        registerAction(helper, "smoke", "qwqqadadadewdqqdwe", HexDir.SOUTH_EAST, OpCook(RecipeType.SMOKING, "target.smoking"))
        registerAction(helper, "blast", "qwqqadadadewweewwe", HexDir.SOUTH_EAST, OpCook(RecipeType.BLASTING, "target.blasting"))
        registerAction(helper, "stonecut", "qqqqqwaeaeaeaeaeadawa", HexDir.EAST, OpStonecut)

        registerAction(helper, "displace", "qaqqqqeedaqqqa", HexDir.NORTH_EAST, OpDisplace)
        registerAction(helper, "absorb_arm", "aaqqadaqwqa", HexDir.WEST, OpAbsorbArm)

        registerAction(helper, "get_evocation", "wwdeeeeeqeaqawwewewwaqawwewew", HexDir.EAST, OpGetEvocation)
        registerAction(helper, "set_evocation", "wwaqqqqqeqdedwwqwqwwdedwwqwqw", HexDir.EAST, OpSetEvocation)
        registerAction(helper, "is_evoking", "wwaqqqqqeeaqawwewewwaqawwewew", HexDir.EAST, OpGetKeybindSelf("key.hexical.evoke"))

        registerAction(helper, "set_lesser_sentinels", "aeaae", HexDir.EAST, OpLesserSentinelSet)
        registerAction(helper, "get_lesser_sentinels", "dqddq", HexDir.WEST, OpLesserSentinelGet)

        registerAction(helper, "list_possessions", "eeeewddwa", HexDir.EAST, OpListPossessions)
        registerAction(helper, "move_item", "qqqqwaawd", HexDir.EAST, OpMoveItem)
        registerAction(helper, "absorb_item", "qqadawaawd", HexDir.EAST, OpAbsorbItem)
        registerAction(helper, "expel_item", "eedadwddwa", HexDir.EAST, OpExpelItem)

        registerAction(helper, "conjure_firework", "dedwaqwwawwqa", HexDir.SOUTH_WEST, OpConjureFirework)

        registerAction(helper, "hunt_sentinel", "dwqwaeawaeqqqwqwqqw", HexDir.NORTH_EAST, OpHuntSentinel)
        registerAction(helper, "exorcise_sentinel", "wdqdwdqedwewaawewd", HexDir.EAST, OpExorciseSentinel)

        registerAction(helper, "shader_clear", "eeeeeqaqeeeee", HexDir.WEST, OpShader(null))
        registerAction(helper, "shader_owl", "edewawede", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/night_vision.json")))
        registerAction(helper, "shader_lines", "eedwwawwdee", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/outlines_only.json")))
        registerAction(helper, "shader_tv", "wewdwewwawwewdwew", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/television.json")))
        registerAction(helper, "shader_media", "eewdweqaqewdwee", HexDir.WEST, OpShader(HexicalMain.id("shaders/post/media.json")))
        registerAction(helper, "shader_spider", "qaqdedaedqqdedaqaedeqd", HexDir.NORTH_EAST, OpShader(HexicalMain.id("shaders/post/spider.json")))

        registerAction(helper, "horrible", "wedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqeedqawqeewdeaqqewdeaqeewdeaqeedqawqqedqawqqewdeaqeedqawqeewdeaqqewdeaqeewdeaqeedqawqqedqawqqewdeaqqedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqqedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqeedqawqeewdeaqeewdeaqqedqawqqedqawqeedqawqqewdeaqqedqawqeewdeaqqewdeaqeewdeaqeedqawqqedqawqqewdeaqe", HexDir.EAST, OpHorrible)

        registerAction(helper, "conjure_speck", "ade", HexDir.SOUTH_WEST, OpConjureSpeck)

        registerAction(helper, "kill_specklike", "adeaqde", HexDir.SOUTH_WEST, OpKillSpecklike)
        registerAction(helper, "move_specklike", "adeqaa", HexDir.SOUTH_WEST, OpSetSpecklikePos)
        registerAction(helper, "rotate_specklike", "adeaw", HexDir.SOUTH_WEST, OpSetSpecklikeRotation)
        registerAction(helper, "roll_specklike", "adeqqqqq", HexDir.SOUTH_WEST, OpSetSpecklikeRoll)
        registerAction(helper, "size_specklike", "adeeqed", HexDir.SOUTH_WEST, OpSetSpecklikeSize)
        registerAction(helper, "thickness_specklike", "adeeqw", HexDir.SOUTH_WEST, OpSetSpecklikeThickness)
        registerAction(helper, "lifetime_specklike", "adeqqaawdd", HexDir.SOUTH_WEST, OpSetSpecklikeLifespan)
        registerAction(helper, "pigment_specklike", "adeqqaq", HexDir.SOUTH_WEST, OpSetSpecklikePigment)

        registerAction(helper, "conjure_strand", "daq", HexDir.SOUTH_EAST, OpConjureStrand)
        registerAction(helper, "draw_pattern", "eadqqqa", HexDir.NORTH_EAST, OpDrawPattern)

        registerAction(helper, "conjure_mesh", "qaqqqqqwqqqdeeweweeaeewewee", HexDir.EAST, OpConjureMesh)

        registerAction(helper, "charm", "edeeeeeqaaqeeeadweeqeeqdqeeqeeqde", HexDir.SOUTH_EAST, OpCharmItem)
        registerAction(helper, "media_color_charmed", "qaqqdwdwd", HexDir.NORTH_EAST, OpMediaColorCharmed)
        registerAction(helper, "discharm", "qaqwddaaeawaea", HexDir.NORTH_EAST, OpDischarmItem)
        registerAction(helper, "write_charmed", "waqqqqqedeqdqdqdqdqe", HexDir.NORTH_EAST, OpWriteCharmed)
        registerAction(helper, "read_charmed", "waqqqqqeaqeaeaeaeaeq", HexDir.NORTH_EAST, OpReadCharmed)
        registerAction(helper, "write_charmed_proxy", "edewqaqqdeeeee", HexDir.SOUTH_EAST, OpProxyWriteCharmed)
        registerAction(helper, "read_charmed_proxy", "qaqwedeeaqqqqq", HexDir.NORTH_EAST, OpProxyReadCharmed)

        registerAction(helper, "greater_blink", "wqawawaqwqwqawawaqw", HexDir.SOUTH_WEST, OpGreaterBlink)
    }

    private fun registerAction(
        helper: RegisterEvent.RegisterHelper<ActionRegistryEntry>,
        name: String,
        signature: String,
        startDir: HexDir,
        action: Action
    ) {
        helper.register(
            HexicalMain.id(name),
            ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        )
    }
}
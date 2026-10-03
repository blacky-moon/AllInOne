package miyucomics.hexical.inits

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.pigments.PigmentIota
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalIota {
    fun registerAll(helper: RegisterEvent.RegisterHelper<IotaType<*>>) {
        helper.register(HexicalMain.id("dye"), DyeIota.TYPE)
        helper.register(HexicalMain.id("pigment"), PigmentIota.TYPE)
    }
}
package miyucomics.hexical.features.gauntlet_staff

import miyucomics.hexical.inits.HexicalItems
import top.theillusivec4.curios.api.client.CuriosRendererRegistry

object GauntletStaffCurioRenderers {
    fun register() {
        CuriosRendererRegistry.register(HexicalItems.GAUNTLET_STAFF.get()) {
            GauntletStaffCurioRenderer.create()
        }
    }
}
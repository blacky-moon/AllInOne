package miyucomics.hexical.misc

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.forge.cap.adimpl.CapEntityIotaHolder

abstract class SimpleEntityIotaHolder : CapEntityIotaHolder() {
    abstract override fun readIota(): Iota?
    abstract override fun writeIota(iota: Iota?, simulate: Boolean): Boolean
    abstract override fun writeable(): Boolean
}
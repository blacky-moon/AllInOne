package miyucomics.hexical.features.specklikes.speck

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.misc.SimpleEntityIotaHolder
import miyucomics.hexpose.iotas.TextIota

class SpeckChronicler(val speck: SpeckEntity) : SimpleEntityIotaHolder() {

    override fun writeable(): Boolean = true

    // В 1.21.1 ADIotaHolder требует readIota(), а не readIotaTag()
    override fun readIota(): Iota {
        val text = speck.entityData.get(SpeckEntity.TEXT_DATA_TRACKER)
        return TextIota(text)
    }

    override fun writeIota(iota: Iota?, simulate: Boolean): Boolean {
        if (iota == null) return false
        if (!simulate) {
            speck.setText(iota.display())
        }
        return true
    }
}
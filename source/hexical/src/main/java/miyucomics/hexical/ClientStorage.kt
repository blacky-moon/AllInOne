package miyucomics.hexical

import miyucomics.hexical.features.media_log.MediaLogField
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
object ClientStorage {
	@JvmField
	var ticks: Int = 0
	var lesserSentinels: MutableList<Vec3> = mutableListOf()
	var mediaLog: MediaLogField = MediaLogField()
}
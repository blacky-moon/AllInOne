package miyucomics.hexical.features.specklikes.strand

import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import miyucomics.hexical.features.specklikes.FigureSpecklike
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class StrandEntity(entityType: EntityType<out StrandEntity>, level: Level) : FigureSpecklike(entityType, level) {

    constructor(level: Level) : this(HexicalEntities.STRAND_ENTITY.get(), level)

    var clientVertices: MutableList<Vec2> = mutableListOf()

    fun getShape(): List<Vec3Iota> {
        val shapeTag = entityData.get(SHAPE_DATA_TRACKER)
        val list = shapeTag.getList("shape", FloatTag.TAG_FLOAT.toInt())
        val deserializedVertices = mutableListOf<Vec3Iota>()
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val x = (iterator.next() as FloatTag).asFloat.toDouble()
            val y = (iterator.next() as FloatTag).asFloat.toDouble()
            deserializedVertices.add(Vec3Iota(Vec3(x, y, 0.0)))
        }
        return deserializedVertices
    }

    fun setShape(shape: List<Vec2>) {
        val compound = CompoundTag()
        val list = ListTag()
        for (vertex in shape) {
            list.add(FloatTag.valueOf(vertex.x))
            list.add(FloatTag.valueOf(vertex.y))
        }
        compound.put("shape", list)
        entityData.set(SHAPE_DATA_TRACKER, compound)
    }

    override fun processShape(shape: CompoundTag) {
        clientVertices.clear()
        val list = shape.getList("shape", FloatTag.TAG_FLOAT.toInt())
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val x = (iterator.next() as FloatTag).asFloat
            val y = (iterator.next() as FloatTag).asFloat
            clientVertices.add(Vec2(x, y))
        }
    }
}
package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3

object ForgeTeleportUtil {
    fun teleport(
        entity: Entity,
        dest: ServerLevel,
        pos: Vec3,
        speed: Vec3 = Vec3.ZERO,
        yRot: Float = entity.yRot,
        xRot: Float = entity.xRot
    ) {
        if (entity.level() === dest) {
            entity.teleportTo(pos.x, pos.y, pos.z)
            entity.deltaMovement = speed
            entity.yRot = yRot
            entity.xRot = xRot
            entity.yHeadRot = yRot
            entity.setYBodyRot(yRot)
            entity.hasImpulse = true
        } else {
            val transition = DimensionTransition(
                dest,
                pos,
                speed,
                yRot,
                xRot
            ) { placed ->
                placed.deltaMovement = speed
                placed.yRot = yRot
                placed.xRot = xRot
                placed.yHeadRot = yRot
                placed.setYBodyRot(yRot)
                placed.hasImpulse = true
            }

            entity.changeDimension(transition)
        }
    }
}
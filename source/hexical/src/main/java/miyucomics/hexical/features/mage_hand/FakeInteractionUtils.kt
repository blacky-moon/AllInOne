package miyucomics.hexical.features.mage_hand

import com.mojang.authlib.GameProfile
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.util.FakePlayerFactory
import net.neoforged.neoforge.common.util.TriState // ИСПРАВЛЕНО: Импортируем TriState вместо Event
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.sqrt

object FakeInteractionUtils {
    private fun createFakePlayer(level: ServerLevel, pos: Vec3, lookDir: Vec3, stack: ItemStack, owner: ServerPlayer?, sneak: Boolean): net.neoforged.neoforge.common.util.FakePlayer {
        val fakePlayer = if (owner == null)
            FakePlayerFactory.get(level, GameProfile(UUID(0, 0), "[Hexical Mage Hand]"))
        else
            FakePlayerFactory.get(level, GameProfile(owner.uuid, "[Hexical Mage Hand]"))

        val pose = if (sneak) Pose.CROUCHING else Pose.STANDING

        fakePlayer.setPos(pos.x, pos.y - fakePlayer.getEyeHeight(pose).toDouble(), pos.z)
        fakePlayer.isShiftKeyDown = sneak

        val horizon = sqrt(lookDir.x * lookDir.x + lookDir.z * lookDir.z)
        val yaw = Math.toDegrees(atan2(-lookDir.x, lookDir.z)).toFloat()
        val pitch = Math.toDegrees(atan2(-lookDir.y, horizon)).toFloat()

        fakePlayer.yRot = yaw
        fakePlayer.yHeadRot = yaw
        fakePlayer.xRot = pitch

        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack)
        return fakePlayer
    }

    fun rightClickAt(level: ServerLevel, stack: ItemStack, owner: ServerPlayer?, position: Vec3, rotation: Vec3, reach: Double = 4.5, sneak: Boolean = false): List<ItemStack> {
        val fakePlayer = createFakePlayer(level, position, rotation.normalize(), stack, owner, sneak)
        val raycastEnd = position.add(rotation.scale(reach))
        val box = AABB(position, raycastEnd).inflate(1.0)

        val blockHit = level.clip(ClipContext(position, raycastEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, fakePlayer))

        val entityHit = ProjectileUtil.getEntityHitResult(
            level,
            fakePlayer,
            position,
            raycastEnd,
            box,
            { entity -> !entity.isSpectator && entity.isPickable },
            blockHit.location.distanceToSqr(position).toFloat()
        )

        val hitResult = entityHit ?: blockHit ?: BlockHitResult.miss(raycastEnd, Direction.getNearest(rotation.x, rotation.y, rotation.z), BlockPos.containing(raycastEnd))

        var actionResult = InteractionResult.PASS
        when (hitResult) {
            is EntityHitResult -> {
                val target = hitResult.entity
                val event = PlayerInteractEvent.EntityInteract(fakePlayer, InteractionHand.MAIN_HAND, target)
                if (!NeoForge.EVENT_BUS.post(event).isCanceled) {
                    actionResult = target.interactAt(fakePlayer, hitResult.location, InteractionHand.MAIN_HAND)
                    if (actionResult == InteractionResult.PASS) {
                        actionResult = fakePlayer.interactOn(target, InteractionHand.MAIN_HAND)
                    }
                } else {
                    actionResult = InteractionResult.FAIL
                }
            }
            is BlockHitResult if hitResult.type != HitResult.Type.MISS -> {
                val event = PlayerInteractEvent.RightClickBlock(fakePlayer, InteractionHand.MAIN_HAND, hitResult.blockPos, hitResult)
                // ИСПРАВЛЕНО: Используем TriState.FALSE вместо Event.Result.DENY
                if (!NeoForge.EVENT_BUS.post(event).isCanceled && event.useBlock != TriState.FALSE) {
                    val blockState = level.getBlockState(hitResult.blockPos)
                    if (!fakePlayer.isShiftKeyDown || fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).isEmpty) {
                        actionResult = blockState.useWithoutItem(level, fakePlayer, hitResult)
                    }
                    if (actionResult == InteractionResult.PASS && event.useItem != TriState.FALSE) {
                        actionResult = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).useOn(UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, hitResult))
                    }
                } else {
                    actionResult = InteractionResult.FAIL
                }
            }
        }

        if (actionResult == InteractionResult.PASS) {
            val itemResult = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).use(level, fakePlayer, InteractionHand.MAIN_HAND)
            if (itemResult.result.consumesAction()) {
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, itemResult.`object`)
            }
        }

        return fakePlayer.inventory.items + fakePlayer.inventory.armor + fakePlayer.inventory.offhand
    }
}
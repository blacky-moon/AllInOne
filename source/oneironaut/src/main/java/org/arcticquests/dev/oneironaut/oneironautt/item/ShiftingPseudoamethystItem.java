package org.arcticquests.dev.oneironaut.oneironautt.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautClient;
import org.arcticquests.dev.oneironaut.oneironautt.network.SpoopyScreamPacket;

import java.util.List;

public class ShiftingPseudoamethystItem extends Item {
    public ShiftingPseudoamethystItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        Level world = entity.level();
        if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
            float pitch = 0.75f + (world.random.nextFloat() / 2);
            PacketDistributor.sendToPlayersNear(
                    serverWorld, null,
                    entity.getX(), entity.getY(), entity.getZ(), 16.0,
                    new SpoopyScreamPacket(SoundEvents.FOX_SCREECH, pitch)
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Level world = context.level();
        if (world != null && world.isClientSide) {
            OneironautClient.lastShiftingHoverTick = world.getGameTime();
            if (OneironautClient.lastHoveredShifting != stack) {
                OneironautClient.lastHoveredShifting = stack;
            }
        }
    }
}
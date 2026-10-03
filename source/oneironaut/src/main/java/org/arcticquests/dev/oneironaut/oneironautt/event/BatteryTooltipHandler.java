package org.arcticquests.dev.oneironaut.oneironautt.event;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class BatteryTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof MediaHolderItem) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.contains("oneironaut_last_media_change")) {
                    long lastChange = tag.getLong("oneironaut_last_media_change");

                    if (lastChange == 0) return;

                    double dustUnits = (double) lastChange / MediaConstants.DUST_UNIT;

                    Component changeText;
                    if (lastChange > 0) {
                        changeText = Component.literal(String.format("+%.2f", dustUnits))
                                .withStyle(ChatFormatting.GREEN);
                    } else {
                        changeText = Component.literal(String.format("%.2f", dustUnits))
                                .withStyle(ChatFormatting.RED);
                    }

                    event.getToolTip().add(changeText);
                }
            }
        }
    }
}
package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

/**
 * BlockItem, который одновременно ведёт себя как посох Hex Casting.
 *
 * - Клик ПКМ по блоку (useOn)  -> обычное поведение BlockItem, ставит блок circle.
 * - Клик ПКМ в воздух (use)    -> логика ItemStaff: открывает каст-GUI.
 *
 * Логика use() скопирована из at.petrak.hexcasting.common.items.ItemStaff,
 * чтобы не пришлось наследоваться от двух классов одновременно
 * (Java не поддерживает множественное наследование классов).
 */
public class CircleBlockItem extends BlockItem {

    public CircleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (player.getAttributeValue(HexAttributes.FEEBLE_MIND) > 0.0) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        if (player.isShiftKeyDown()) {
            if (world.isClientSide()) {
                player.playSound(HexSounds.STAFF_RESET, 1.0F, 1.0F);
            } else if (player instanceof ServerPlayer serverPlayer) {
                IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
                var packet = new MsgClearSpiralPatternsS2C(player.getUUID());
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer, packet);
                IXplatAbstractions.INSTANCE.sendPacketTracking(serverPlayer, packet);
            }
        }

        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(serverPlayer, hand);
            List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(serverPlayer);

            @Nullable CompoundTag ravenmind = vm.getImage().ravenmind().orElse(null);

            IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer,
                    new MsgOpenSpellGuiS2C(hand, patterns, vm.getImage().getStack(), ravenmind, 0));
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
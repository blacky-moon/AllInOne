package org.blacky_moon.hexverse.common.item;

import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BaseAmethystItem {

    public static final Tier AMETHYST_TIER = new Tier() {
        @Override
        public int getUses() {
            return (int) (Tiers.DIAMOND.getUses() * 0.45);
        }

        @Override
        public float getSpeed() {
            return Tiers.DIAMOND.getSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return Tiers.DIAMOND.getAttackDamageBonus();
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return Tiers.DIAMOND.getIncorrectBlocksForDrops();
        }

        @Override
        public int getEnchantmentValue() {
            return Tiers.DIAMOND.getEnchantmentValue();
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(HexItems.CHARGED_AMETHYST);
        }
    };

}
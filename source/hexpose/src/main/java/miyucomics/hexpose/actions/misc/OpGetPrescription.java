package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;

public class OpGetPrescription implements ConstMediaAction {
    public static final OpGetPrescription INSTANCE = new OpGetPrescription();

    private OpGetPrescription() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);

        if (arg instanceof EntityIota) {
            Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
            if (entity == null) {
                return List.of(new ListIota(new ArrayList<>()));
            }

            env.assertEntityInRange(entity);

            if (entity instanceof ItemEntity itemEntity) {
                return handleItemStack(itemEntity.getItem(), args);
            } else if (entity instanceof Arrow arrow) {
                List<Iota> output = new ArrayList<>();
                ItemStack stack = arrow.getPickupItemStackOrigin();
                PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                for (MobEffectInstance instance : potionContents.getAllEffects()) {
                    output.add(new IdentifierIota(BuiltInRegistries.MOB_EFFECT.getKey(instance.getEffect().value())));
                }
                return List.of(new ListIota(output));
            } else if (entity instanceof ThrownPotion thrownPotion) {
                List<Iota> effects = new ArrayList<>();
                ItemStack stack = thrownPotion.getItem();
                PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                for (MobEffectInstance effect : potionContents.getAllEffects()) {
                    effects.add(new IdentifierIota(BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value())));
                }
                return List.of(new ListIota(effects));
            } else if (entity instanceof ShulkerBullet) {
                return List.of(new ListIota(List.of(new IdentifierIota(BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.LEVITATION.value())))));
            } else if (entity instanceof WitherSkull) {
                return List.of(new ListIota(List.of(new IdentifierIota(BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.WITHER.value())))));
            } else {
                return List.of(new ListIota(new ArrayList<>()));
            }
        } else if (arg instanceof ItemStackIota itemStackIota) {
            return handleItemStack(itemStackIota.getStack(), args);
        } else {
            throw MishapInvalidIota.of(args.get(0), 0, "potion_holding");
        }
    }

    private List<Iota> handleItemStack(ItemStack stack, List<? extends Iota> args) {
        if (!(stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION) ||
                stack.getItem().getFoodProperties(stack, null) != null || stack.is(Items.TIPPED_ARROW))) {
            throw MishapInvalidIota.of(args.get(0), 0, "potion_holding");
        }

        List<Iota> effects = new ArrayList<>();

        PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        for (MobEffectInstance effect : potionContents.getAllEffects()) {
            effects.add(new IdentifierIota(BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value())));
        }

        var foodProperties = stack.getItem().getFoodProperties(stack, null);
        if (foodProperties != null) {
            for (var pair : foodProperties.effects()) {
                effects.add(new IdentifierIota(BuiltInRegistries.MOB_EFFECT.getKey(pair.effect().getEffect().value())));
            }
        }

        return List.of(new ListIota(effects));
    }
}
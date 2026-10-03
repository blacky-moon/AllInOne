package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota; // Импортируем ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpGetArmor implements ConstMediaAction {
    public static final OpGetArmor INSTANCE = new OpGetArmor();

    private OpGetArmor() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof EntityIota)) {
            throw MishapInvalidIota.ofType(arg, 0, "entity");
        }
        var entity = ((EntityIota) arg).getEntity(env.getWorld());
        if (entity == null) {
            return List.of(new NullIota());
        }
        env.assertEntityInRange(entity);

        List<Iota> armorItems = new ArrayList<>();

        if (entity instanceof LivingEntity living) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.isArmor()) {
                    ItemStack stack = living.getItemBySlot(slot);
                    // Защита: добавляем только если слот не пустой (не воздух)
                    if (!stack.isEmpty()) {
                        armorItems.add(new ItemStackIota(stack));
                    }
                }
            }
        } else {
            return List.of(new NullIota());
        }

        // Возвращаем как ОДНУ ListIota (список предметов брони на сущности)
        return List.of(new ListIota(armorItems));
    }
}
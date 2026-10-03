package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota; // Не забудьте импорт!
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpGetInventory implements ConstMediaAction {
    public static final OpGetInventory INSTANCE = new OpGetInventory();

    private OpGetInventory() {}

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
        Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
        if (entity == null) {
            return List.of(new NullIota());
        }
        env.assertEntityInRange(entity);

        List<Iota> inventoryItems = new ArrayList<>();

        if (entity instanceof AbstractHorse horse) {
            for (int i = 0; i < horse.getInventory().getContainerSize(); i++) {
                ItemStack stack = horse.getInventory().getItem(i);
                // Защита: пропускаем воздух/пустые слоты, чтобы не сломать кодеки 1.21
                if (!stack.isEmpty()) {
                    inventoryItems.add(new ItemStackIota(stack));
                }
            }
        } else if (entity instanceof ItemFrame itemFrame) {
            ItemStack stack = itemFrame.getItem();
            if (!stack.isEmpty()) {
                inventoryItems.add(new ItemStackIota(stack));
            }
        } else if (entity instanceof Player player) {
            if (env.getCastingEntity() != entity) {
                throw new MishapOthersName(player);
            }
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    inventoryItems.add(new ItemStackIota(stack));
                }
            }
        } else if (entity instanceof ContainerEntity containerEntity) {
            for (int i = 0; i < containerEntity.getContainerSize(); i++) {
                ItemStack stack = containerEntity.getItem(i);
                if (!stack.isEmpty()) {
                    inventoryItems.add(new ItemStackIota(stack));
                }
            }
        } else {
            return List.of(new NullIota());
        }

        // Возвращаем ОДНУ ListIota, содержащую весь список предметов
        return List.of(new ListIota(inventoryItems));
    }
}
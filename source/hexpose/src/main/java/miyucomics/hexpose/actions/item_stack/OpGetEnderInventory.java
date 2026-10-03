package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota; // Импортируем ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OpGetEnderInventory implements ConstMediaAction {
    public static final OpGetEnderInventory INSTANCE = new OpGetEnderInventory();

    private OpGetEnderInventory() {}

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        var entity = env.getCastingEntity();
        if (!(entity instanceof Player)) {
            throw new MishapBadCaster();
        }

        Player player = (Player) entity;
        List<Iota> enderItems = new ArrayList<>();

        for (int i = 0; i < player.getEnderChestInventory().getContainerSize(); i++) {
            ItemStack stack = player.getEnderChestInventory().getItem(i);
            // Защита от краша кодеков 1.21.1 — пропускаем пустые ячейки
            if (!stack.isEmpty()) {
                enderItems.add(new ItemStackIota(stack));
            }
        }

        // Оборачиваем в ListIota, чтобы выдать результат единым списком
        return List.of(new ListIota(enderItems));
    }
}
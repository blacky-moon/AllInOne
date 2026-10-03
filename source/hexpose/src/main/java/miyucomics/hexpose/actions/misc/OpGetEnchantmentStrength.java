package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;

public class OpGetEnchantmentStrength implements ConstMediaAction {
    public static final OpGetEnchantmentStrength INSTANCE = new OpGetEnchantmentStrength();

    private OpGetEnchantmentStrength() {}

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg0 = args.get(0);
        if (!(arg0 instanceof ItemStackIota)) {
            throw MishapInvalidIota.ofType(arg0, 0, "item_stack");
        }
        ItemStack stack = ((ItemStackIota) arg0).getStack();

        Iota arg1 = args.get(1);
        if (!(arg1 instanceof IdentifierIota)) {
            throw MishapInvalidIota.ofType(arg1, 1, "identifier");
        }
        ResourceLocation id = ((IdentifierIota) arg1).getIdentifier();
        var registryAccess = env.getWorld().registryAccess();
        var enchantmentRegistry = registryAccess.registry(Registries.ENCHANTMENT)
                .orElseThrow(() -> new IllegalStateException("Enchantment registry not found"));

        if (!enchantmentRegistry.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(1), 0, "enchantment_id");
        }

        var holder = enchantmentRegistry.getHolder(id)
                .orElseThrow(() -> MishapInvalidIota.of(args.get(1), 0, "enchantment_id"));

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        int level = enchantments.getLevel(holder);

        return List.of(new DoubleIota(level));
    }
}
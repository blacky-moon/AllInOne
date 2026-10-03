package ram.talia.moreiotas;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class MoreIotasConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue maxMatrixSize = BUILDER
            .comment("The maximum matrix width/height.")
            .defineInRange("maxMatrixSize",144, 3,512);

    public static final ModConfigSpec.IntValue maxStringSize = BUILDER
            .comment("The maximum length of a string.")
            .defineInRange("maxStringSize", 1728, 1, 32768);

    public static final ModConfigSpec.DoubleValue setBlockStringCost = BUILDER
            .comment("The cost for writing a string to a given block, in dust.")
            .defineInRange("setBlockStringCost", 0.01d, 0.0001d, 10_000d);

    public static final ModConfigSpec.DoubleValue nameCost = BUILDER
            .comment("The cost of naming an entity with the Name spell.")
            .defineInRange("nameCost", 0.01d, 0.0001d, 10_000d);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}

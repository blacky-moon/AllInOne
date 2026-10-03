package miyucomics.hexpose.iotas;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemStackIota extends Iota {
    public static final String TAG_STACK_ID = "hexpose:stack_id";
    public static final String TAG_STACK_COUNT = "hexpose:stack_count";

    private final ItemStack stack;

    public ItemStackIota(ItemStack stack) {
        super(() -> TYPE);
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isTruthy() {
        return !stack.isEmpty();
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that) && that instanceof ItemStackIota && ItemStack.matches(this.stack, ((ItemStackIota) that).stack);
    }

    public Tag serialize() {
        CompoundTag tag = new CompoundTag();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        tag.putString(TAG_STACK_ID, itemId.toString());
        tag.putInt(TAG_STACK_COUNT, stack.getCount());
        // В 1.21 нет getTag() - используем компоненты
        DataComponentPatch patch = stack.getComponentsPatch();
        if (!patch.isEmpty()) {
            // Сериализуем компоненты
            var result = DataComponentPatch.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, patch);
            if (result.isSuccess()) {
                tag.put("components", result.getOrThrow());
            }
        }
        return tag;
    }

    @Override
    public Component display() {
        if (stack.isEmpty()) {
            return Component.translatable("hexpose.item_stack.null").withStyle(ChatFormatting.GRAY);
        }
        return Component.literal("[item:" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "]")
                .append(Component.translatable("hexpose.item_stack.format",
                        Component.empty().append(stack.getHoverName()).withStyle(stack.getRarity().color()),
                        stack.getCount()));
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(stack);
    }

    public static final IotaType<ItemStackIota> TYPE = new IotaType<ItemStackIota>() {
        private static final MapCodec<ItemStackIota> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("stack").forGetter(ItemStackIota::getStack)
                ).apply(instance, ItemStackIota::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, ItemStackIota> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC,
                        ItemStackIota::getStack,
                        ItemStackIota::new
                );

        @Override
        public MapCodec<ItemStackIota> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemStackIota> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public int color() {
            return 0xff_fc0362;
        }

        public Component display(Tag tag) {
            ItemStack stack = deserialize(tag, null).getStack();
            if (stack.isEmpty()) {
                return Component.translatable("hexpose.item_stack.null").withStyle(ChatFormatting.GRAY);
            }
            CompoundTag compound = (CompoundTag) tag;
            return Component.literal("[item:" + compound.getString(TAG_STACK_ID) + "]")
                    .append(Component.translatable("hexpose.item_stack.format",
                            Component.empty().append(stack.getHoverName()).withStyle(stack.getRarity().color()),
                            stack.getCount()));
        }

        public ItemStackIota deserialize(Tag tag, ServerLevel world) {
            CompoundTag compound = (CompoundTag) tag;
            ResourceLocation itemId = ResourceLocation.parse(compound.getString(TAG_STACK_ID));
            int count = compound.getInt(TAG_STACK_COUNT);
            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(itemId), count);

            // Восстанавливаем компоненты если есть
            if (compound.contains("components")) {
                var result = DataComponentPatch.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, compound.get("components"));
                if (result.isSuccess()) {
                    stack.applyComponents(result.getOrThrow());
                }
            }

            return new ItemStackIota(stack);
        }
    };

    public static ItemStackIota createOptimized(ItemStack originalStack) {
        // В 1.21 просто копируем стек с компонентами
        return new ItemStackIota(originalStack.copy());
    }

    // Вспомогательные методы
    public static List<Iota> asActionResult(ItemStack stack) {
        return List.of(createOptimized(stack));
    }

    public static ItemStack getItemStack(List<Iota> list, int idx, int argc) {
        if (idx >= list.size()) {
            throw new MishapNotEnoughArgs(idx + 1, list.size());
        }
        Iota x = list.get(idx);
        if (x instanceof ItemStackIota) {
            return ((ItemStackIota) x).getStack().copy();
        }
        throw MishapInvalidIota.ofType(x, argc == 0 ? idx : argc - (idx + 1), "item_stack");
    }

    public static ItemStack getItemStack(List<Iota> list, int idx) {
        return getItemStack(list, idx, 0);
    }
}
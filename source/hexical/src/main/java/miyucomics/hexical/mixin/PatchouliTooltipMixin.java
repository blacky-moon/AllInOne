package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexical.compat.HexicalCompat;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.patchouli.client.handler.TooltipHandler;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

@Mixin(value = TooltipHandler.class, remap = false)
public class PatchouliTooltipMixin {

    @WrapOperation(method = "onTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getSelectionSize()I"))
    private static int hexical$addCuriosSlot(Operation<Integer> original) {
        return original.call() + 1;
    }

    @WrapOperation(method = "onTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getItem(I)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack hexical$injectGauntlet(Inventory instance, int i, Operation<ItemStack> original) {
        if (i == 9) {
            Player player = instance.player;
            if (HexicalCompat.INSTANCE.isGauntletStaffEquipped(player)) {
                ItemStack gauntlet = HexicalCompat.INSTANCE.getGauntletStaff(player);
                if (gauntlet != null) return gauntlet;
            }
            return ItemStack.EMPTY;
        }
        return original.call(instance, i);
    }

    @WrapOperation(method = "onTooltip", at = @At(value = "INVOKE", target = "Lvazkii/patchouli/common/util/ItemStackUtil;getBookFromStack(Lnet/minecraft/world/item/ItemStack;)Lvazkii/patchouli/common/book/Book;"))
    private static Book hexical$pretendGauntletIsBook(ItemStack stack, Operation<Book> original) {
        if (stack.getItem() == HexicalItems.GAUNTLET_STAFF.get()) {
            return BookRegistry.INSTANCE.books.get(ResourceLocation.fromNamespaceAndPath("hexcasting", "thehexbook"));
        }
        return original.call(stack);
    }

    @WrapOperation(method = "onTooltip", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Inventory;selected:I", opcode = Opcodes.PUTFIELD))
    private static void hexical$preventInvalidHotbarSwitch(Inventory instance, int value, Operation<Void> original) {
        if (value < 9) {
            original.call(instance, value);
        }
    }
}
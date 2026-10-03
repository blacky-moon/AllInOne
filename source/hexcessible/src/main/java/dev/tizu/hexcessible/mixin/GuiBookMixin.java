package dev.tizu.hexcessible.mixin;

import dev.tizu.hexcessible.entries.BookEntries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.book.Book;

@Mixin(GuiBook.class)
public abstract class GuiBookMixin {

    @Shadow @Final
    public Book book;

    @Shadow
    void changePage(boolean left, boolean sfx) {}

    @Inject(
            method = "mouseScrolled(DDDD)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY,
                                 CallbackInfoReturnable<Boolean> cir) {
        if (this.book == null || !this.book.id.equals(BookEntries.BOOKID)) {
            return;
        }

        if (scrollY > 0) {
            this.changePage(true, true);
            cir.setReturnValue(true);
        } else if (scrollY < 0) {
            this.changePage(false, true);
            cir.setReturnValue(true);
        }
    }
}
package miyucomics.hexical.mixin;

import miyucomics.hexical.HexicalMain;
import miyucomics.hexical.features.patchouli.BlockPage;
import miyucomics.hexical.features.patchouli.DyeingBlockPage;
import miyucomics.hexical.features.patchouli.DyeingItemPage;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.ClientBookRegistry;

import java.util.Map;

@Mixin(value = ClientBookRegistry.class)
public class ClientBookRegistryMixin {
    @Shadow
    @Final
    public Map<ResourceLocation, Class<? extends BookPage>> pageTypes;

    @Inject(method = "addPageTypes", at = @At("HEAD"), remap = true)
    public void addHexicalPageTypes(CallbackInfo ci) {
        pageTypes.put(HexicalMain.id("block"), BlockPage.class);
        pageTypes.put(HexicalMain.id("dyeing_block"), DyeingBlockPage.class);
        pageTypes.put(HexicalMain.id("dyeing_item"), DyeingItemPage.class);
    }
}
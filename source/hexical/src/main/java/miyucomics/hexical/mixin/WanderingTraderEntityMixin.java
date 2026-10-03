package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexical.HexicalMain;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillager {
    @SuppressWarnings("unused")
    public WanderingTraderEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "updateTrades", at = @At("RETURN"))
    protected void addNewTrades(CallbackInfo ci) {
        MerchantOffers tradeOfferList = getOffers();

        // Используем INSTANCE для доступа к методам Kotlin object
        if (random.nextFloat() < 0.5) {
            ItemStack trade = HexicalItems.INSTANCE.randomPlush(); // метод экземпляра
            // Конструктор MerchantOffer(ItemStack, ItemStack, int, int, float)
            tradeOfferList.add(new MerchantOffer(new ItemCost(Items.EMERALD, 3), trade, 1, 1, 1.0f));
        }
        if (random.nextFloat() < 0.4) {
            // Доступ к публичному полю через JvmField
            ItemStack trade = new ItemStack(HexicalItems.HAND_LAMP_ITEM.get()); // если поле публичное
            // Если HAND_LAMP_ITEM всё ещё приватное, нужно сделать его @JvmField или добавить геттер.
            // Здесь предполагаем, что сделали @JvmField
            IXplatAbstractions.INSTANCE.findHexHolder(trade).writeHex(new ArrayList<>(), null, MediaConstants.DUST_UNIT * 320);
            IXplatAbstractions.INSTANCE.findMediaHolder(trade).withdrawMedia((int) (HexicalMain.RANDOM.nextFloat() * 160f) * MediaConstants.DUST_UNIT, false);
            tradeOfferList.add(new MerchantOffer(new ItemCost(Items.EMERALD, 32), trade, 1, 1, 1.0f));
        }
        if (random.nextFloat() < 0.1) {
            ItemStack trade = new ItemStack(HexicalItems.ARCH_LAMP_ITEM.get());
            IXplatAbstractions.INSTANCE.findHexHolder(trade).writeHex(new ArrayList<>(), null, MediaConstants.DUST_UNIT * 320);
            IXplatAbstractions.INSTANCE.findMediaHolder(trade).withdrawMedia((int) (HexicalMain.RANDOM.nextFloat() * 160f) * MediaConstants.DUST_UNIT, false);
            tradeOfferList.add(new MerchantOffer(new ItemCost(Items.EMERALD, 64), trade, 1, 1, 1.0f));
        }
    }
}
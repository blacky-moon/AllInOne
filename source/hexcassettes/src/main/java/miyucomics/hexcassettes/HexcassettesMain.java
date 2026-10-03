package miyucomics.hexcassettes;

import at.petrak.hexcasting.common.lib.hex.HexActions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import miyucomics.hexcassettes.data.CassetteState;
import miyucomics.hexcassettes.inits.HexcassettesActions;
import miyucomics.hexcassettes.inits.HexcassettesNetworking;
import miyucomics.hexcassettes.inits.HexcassettesSounds;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;
import java.util.function.Supplier;

@Mod(HexcassettesMain.MOD_ID)
public class HexcassettesMain {
    public static final String MOD_ID = "hexcassettes";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);
    public static final DeferredRegister<net.minecraft.advancements.CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, MOD_ID);

    public static final Supplier<Item> CASSETTE = ITEMS.register("cassette", CassetteItem::new);

    public static final Supplier<SimpleCustomTrigger> QUINIO = TRIGGERS.register("quinio", SimpleCustomTrigger::new);
    public static final Supplier<SimpleCustomTrigger> TAPE_WORM = TRIGGERS.register("tape_worm", SimpleCustomTrigger::new);
    public static final Supplier<SimpleCustomTrigger> FULL_ARSENAL = TRIGGERS.register("full_arsenal", SimpleCustomTrigger::new);

    public HexcassettesMain(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        TRIGGERS.register(modEventBus);
        HexcassettesSounds.SOUNDS.register(modEventBus);

        modEventBus.addListener(this::registerAll);
        modEventBus.addListener(this::addItemsToTab);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

        HexcassettesNetworking.init(modEventBus);
        HexcassettesSounds.init();
    }

    private void registerAll(RegisterEvent event) {
        event.register(HexActions.REGISTRY.key(), HexcassettesActions::registerAll);
    }

    private void addItemsToTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("hexcasting", "hexcasting"))) {
            event.accept(CASSETTE.get());
        }
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            int oldOwned = ((PlayerEntityMinterface) event.getOriginal()).getCassetteState().owned;
            ((PlayerEntityMinterface) event.getEntity()).getCassetteState().owned = oldOwned;
        }
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player instanceof PlayerEntityMinterface minterface) {
            minterface.getCassetteState().sync(player);
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String serializeKey(Component component, HolderLookup.Provider provider) {
        return Component.Serializer.toJson(component, provider);
    }

    public static Component deserializeKey(String string, HolderLookup.Provider provider) {
        return Component.Serializer.fromJson(string, provider);
    }

    public static class CassetteItem extends Item {
        public CassetteItem() {
            super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON).food(new FoodProperties.Builder().alwaysEdible().build()));
        }

        @Override
        public int getUseDuration(ItemStack stack, LivingEntity entity) {
            return 100;
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
            if (level.isClientSide()) {
                level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                        HexcassettesSounds.CASSETTE_INSERT.get(), SoundSource.MASTER, 5f, 1f);
                return super.finishUsingItem(stack, level, livingEntity);
            }

            if (!(livingEntity instanceof ServerPlayer player))
                return super.finishUsingItem(stack, level, livingEntity);

            int maxCassettes = HexcassettesConfiguration.getInstance().maxCassettes;
            CassetteState cassetteData = ((PlayerEntityMinterface) player).getCassetteState();

            if (cassetteData.owned < maxCassettes) {
                TAPE_WORM.get().trigger(player);
                cassetteData.owned += 1;
                if (cassetteData.owned == maxCassettes) {
                    FULL_ARSENAL.get().trigger(player);
                }
                cassetteData.sync(player);
            }
            return super.finishUsingItem(stack, level, livingEntity);
        }
    }

    public static class SimpleCustomTrigger extends SimpleCriterionTrigger<SimpleCustomTrigger.Instance> {
        @Override
        public Codec<Instance> codec() {
            return Instance.CODEC;
        }

        public void trigger(ServerPlayer player) {
            this.trigger(player, instance -> true);
        }

        public record Instance(Optional<ContextAwarePredicate> playerPredicate) implements SimpleInstance {
            public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance ->
                    instance.group(
                            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Instance::playerPredicate)
                    ).apply(instance, Instance::new)
            );

            @Override
            public Optional<ContextAwarePredicate> player() {
                return playerPredicate;
            }
        }
    }
}
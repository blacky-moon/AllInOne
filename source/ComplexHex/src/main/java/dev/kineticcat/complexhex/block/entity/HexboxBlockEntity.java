package dev.kineticcat.complexhex.block.entity;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import dev.kineticcat.complexhex.api.casting.eval.HexboxCastEnv;
import dev.kineticcat.complexhex.block.HexboxBlock;
import dev.kineticcat.complexhex.item.ComplexHexItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HexboxBlockEntity extends HexBlockEntity implements WorldlyContainer, ADMediaHolder {
    private static final DecimalFormat DUST_AMOUNT = new DecimalFormat("###,###.##");
    private static final long MAX_CAPACITY = 9_000_000_000_000_000_000L;

    public static final String TAG_DATA = "Data";
    public static final String TAG_MEDIA = "Media";
    public static final String TAG_PIGMENT = "Pigment";
    public static final String TAG_ERROR_MSG = "ErrorMsg";
    public static final String TAG_ERROR_DISPLAY = "ErrorDisplay";
    public static final String TAG_OWNER_UUID = "OwnerUUID";
    public static final String TAG_OWNER_NAME = "OwnerName";
    public static final String TAG_IOTA = "InternalIota";

    // Исправленный конструктор: передаем все 3 аргумента в супер
    public HexboxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ComplexHexBlockEntities.HEXBOX, blockPos, blockState);
    }

    private boolean initialised = false;
    private UUID ownerUUID = null;
    private String ownerName = null;
    private net.neoforged.neoforge.common.util.FakePlayer fake;

    public ListTag hexTag = null;
    public long media = 0;
    public FrozenPigment pigment = FrozenPigment.DEFAULT.get();
    protected Component displayMsg = null;
    protected ItemStack displayItem = null;
    public CompoundTag iotaTag = null;

    public void sync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void clearDisplay() {
        this.displayMsg = null;
        this.displayItem = null;
        this.sync();
    }

    public void postDisplay(Component error, ItemStack display) {
        this.displayMsg = error;
        this.displayItem = display;
        this.sync();
    }

    public void postMishap(Component mishapDisplay) {
        this.postDisplay(mishapDisplay, new ItemStack(Items.MUSIC_DISC_11));
    }

    public void postPrint(Component printDisplay) {
        this.postDisplay(printDisplay, new ItemStack(Items.BOOK));
    }

    public void setHex(ListIota hex) {
        if (hex != null) {
            var result = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, hex);
            this.hexTag = (ListTag) result.result().orElse(null);
        } else {
            this.hexTag = null;
        }
        sync();
    }

    public void setHex(ListTag hex) {
        this.hexTag = hex;
        sync();
    }

    public ListIota getHex(ServerLevel level) {
        if (hexTag == null) return null;
        var result = IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, hexTag);
        return (ListIota) result.result().orElse(null);
    }

    public void setIota(Iota iota) {
        if (iota != null) {
            var result = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, iota);
            this.iotaTag = (CompoundTag) result.result().orElse(null);
        } else {
            this.iotaTag = null;
        }
    }

    public Iota getIota(ServerLevel level) {
        if (iotaTag == null) return null;
        var result = IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, iotaTag);
        return result.result().orElse(null);
    }

    @Override
    public long getMedia() {
        return media;
    }

    @Override
    public long getMaxMedia() {
        return MAX_CAPACITY;
    }

    public void setMedia(long media) {
        this.media = media;
        sync();
    }

    @Override
    public boolean canRecharge() {
        return true;
    }

    @Override
    public boolean canProvide() {
        return true;
    }

    @Override
    public int getConsumptionPriority() {
        return 0;
    }

    @Override
    public boolean canConstructBattery() {
        return false;
    }

    public void setOwner(net.minecraft.server.level.ServerPlayer player) {
        this.ownerName = player.getGameProfile().getName();
        this.ownerUUID = player.getGameProfile().getId();
        this.pigment = IXplatAbstractions.INSTANCE.getPigment(player);
        sync();
    }

    @Override
    protected void saveModData(CompoundTag ctag, HolderLookup.Provider provider) {
        if (hexTag != null) {
            ctag.put(TAG_DATA, hexTag);
        }
        ctag.putLong(TAG_MEDIA, media);

        if (ownerUUID != null && ownerName != null) {
            ctag.putUUID(TAG_OWNER_UUID, ownerUUID);
            ctag.putString(TAG_OWNER_NAME, ownerName);
        }

        // Сохранение пигмента через Codec
        var pigResult = FrozenPigment.CODEC.encodeStart(NbtOps.INSTANCE, pigment);
        pigResult.result().ifPresent(tag -> ctag.put(TAG_PIGMENT, tag));

        if (this.displayMsg != null && this.displayItem != null) {
            ctag.putString(TAG_ERROR_MSG, Component.Serializer.toJson(this.displayMsg, provider));
            var itemTag = new CompoundTag();
            this.displayItem.saveOptional(provider);
            ctag.put(TAG_ERROR_DISPLAY, itemTag);
        }

        if (iotaTag != null) {
            ctag.put(TAG_IOTA, iotaTag);
        }
    }

    @Override
    public void loadModData(CompoundTag ctag, HolderLookup.Provider provider) {
        if (ctag.contains(TAG_DATA)) {
            hexTag = ctag.getList(TAG_DATA, Tag.TAG_COMPOUND);
        } else {
            hexTag = null;
        }

        if (ctag.contains(TAG_MEDIA)) {
            media = ctag.getLong(TAG_MEDIA);
        } else {
            media = 0;
        }

        if (ctag.contains(TAG_PIGMENT)) {
            var pigResult = FrozenPigment.CODEC.parse(NbtOps.INSTANCE, ctag.getCompound(TAG_PIGMENT));
            pigment = pigResult.result().orElse(FrozenPigment.DEFAULT.get());
        } else {
            pigment = FrozenPigment.DEFAULT.get();
        }

        if (ctag.contains(TAG_ERROR_MSG, Tag.TAG_STRING) && ctag.contains(TAG_ERROR_DISPLAY, Tag.TAG_COMPOUND)) {
            var msg = Component.Serializer.fromJson(ctag.getString(TAG_ERROR_MSG), provider);
            var display = ItemStack.parseOptional(provider, ctag.getCompound(TAG_ERROR_DISPLAY));
            this.displayMsg = msg;
            this.displayItem = display;
        } else {
            this.displayMsg = null;
            this.displayItem = null;
        }

        if (ctag.contains(TAG_OWNER_UUID) && ctag.contains(TAG_OWNER_NAME)) {
            ownerUUID = ctag.getUUID(TAG_OWNER_UUID);
            ownerName = ctag.getString(TAG_OWNER_NAME);
        } else {
            ownerUUID = null;
            ownerName = null;
        }

        if (ctag.contains(TAG_IOTA)) {
            iotaTag = ctag.getCompound(TAG_IOTA);
        } else {
            iotaTag = null;
        }
    }

    private void initialise() {
        if (level instanceof ServerLevel slevel) {
            GameProfile fakeprofile = new GameProfile(
                    ownerUUID != null ? ownerUUID : UUID.randomUUID(),
                    (ownerName != null ? ownerName : "Unknown") + "'s_hexbox"
            );
            fake = FakePlayerFactory.get(slevel, fakeprofile);
            fake.setPos(getBlockPos().below(2).getCenter());
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HexboxBlockEntity box) {
        if (!box.initialised) {
            box.initialise();
            box.initialised = true;
        }
        if (!state.getValue(HexboxBlock.ACTIVATED)) return;
        if (box.hexTag == null) return;
        if (level != null && level.isClientSide()) return;

        var slevel = (ServerLevel) level;
        var env = new HexboxCastEnv(slevel, box.fake, box.pigment, pos);
        var hex = box.getHex(slevel);
        if (hex == null) return;

        List<Iota> instrs = new ArrayList<>();
        hex.subIotas().forEach(instrs::add);

        var vm = CastingVM.empty(env);
        var iota = box.getIota(slevel);

        // В 1.21 сигнатура copy изменилась. Проверь точное количество аргументов в твоей версии Hex Casting.
        // Обычно добавился boolean параметр перед opsConsumed или userData.
        vm.setImage(vm.getImage().copy(
                iota != null ? List.of(iota) : List.of(),
                vm.getImage().getParenCount(),
                vm.getImage().getParenthesized(),
                vm.getImage().getEscapeNext(),
                false, // <--- Новый параметр (предположительно isEscaping или аналогичный)
                vm.getImage().getOpsConsumed(),
                vm.getImage().getUserData()
        ));

        vm.queueExecuteAndWrapIotas(instrs, box.fake.serverLevel());
    }

    public void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines, Level level) {
        if (media < 0) {
            lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), ItemCreativeUnlocker.infiniteMedia(level)));
        } else {
            float dust = (float) media / MediaConstants.DUST_UNIT;
            Component comp = Component.translatable("hexcasting.tooltip.media", DUST_AMOUNT.format(dust));
            lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), comp));
        }

        if (hexTag != null && !hexTag.isEmpty()) {
            var result = IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, hexTag);
            result.result().ifPresent(iota -> {
                lines.add(new Pair<>(
                        new ItemStack(ComplexHexItems.QUENCHED_RECORD),
                        iota.display()
                ));
            });
        }

        if (this.displayMsg != null && this.displayItem != null) {
            lines.add(new Pair<>(this.displayItem, this.displayMsg));
        }
    }

    public void setInfiniteMedia() {
        this.media = -1;
        this.sync();
    }

    public long remainingMediaCapacity() {
        if (this.media < 0) return 0;
        return Math.max(0, MAX_CAPACITY - this.media);
    }

    public long extractMediaFromInsertedItem(ItemStack stack, boolean simulate) {
        if (this.media < 0) return 0;
        return MediaHelper.extractMedia(stack, remainingMediaCapacity(), true, simulate);
    }

    public void insertMedia(ItemStack stack) {
        if (media >= 0 && !stack.isEmpty() && stack.getItem() == HexItems.CREATIVE_UNLOCKER) {
            setInfiniteMedia();
            stack.shrink(1);
        } else {
            var mediamount = extractMediaFromInsertedItem(stack, false);
            if (mediamount > 0) {
                this.media = Math.min(mediamount + media, MAX_CAPACITY);
                this.sync();
            }
        }
    }

    private static final int[] SLOTS = {0};
    @Override
    public int[] getSlotsForFace(Direction direction) { return SLOTS; }
    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) { return canPlaceItem(i, itemStack); }
    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) { return false; }
    @Override
    public int getContainerSize() { return 1; }
    @Override
    public boolean isEmpty() { return true; }
    @Override
    public ItemStack getItem(int i) { return ItemStack.EMPTY.copy(); }
    @Override
    public ItemStack removeItem(int i, int j) { return ItemStack.EMPTY.copy(); }
    @Override
    public ItemStack removeItemNoUpdate(int i) { return ItemStack.EMPTY.copy(); }
    @Override
    public void setItem(int i, ItemStack itemStack) { insertMedia(itemStack); }
    @Override
    public boolean stillValid(Player player) { return false; }
    @Override
    public void clearContent() {}

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (remainingMediaCapacity() == 0) return false;
        if (stack.is(HexItems.CREATIVE_UNLOCKER)) return true;
        var mediamount = extractMediaFromInsertedItem(stack, true);
        return mediamount > 0;
    }
}
package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import ram.talia.moreiotas.api.casting.iota.ItemTypeIota;

public class ItemTypeParser implements INbt2Str<ItemTypeIota> {

    @Override
    public Class<ItemTypeIota> getType() {
        return ItemTypeIota.class;
    }

    @Override
    public String parse(ItemTypeIota iota) {
        // Определяем, является ли это блоком или просто предметом
        boolean isBlock = iota.getBlock() != null;

        // Получаем ResourceLocation через регистры
        String key;
        String prefix;

        if (isBlock) {
            Block block = iota.getBlock();
            key = BuiltInRegistries.BLOCK.getKey(block).getPath();
            prefix = "block";
        } else {
            Item item = iota.getItem();
            key = BuiltInRegistries.ITEM.getKey(item).getPath();
            prefix = "item";
        }

        // Используем твой процессор для удаления префикса minecraft, если нужно
        String processedKey = StringProcessors.OMIT_MC.apply(key);

        // Формируем итоговую строку. 
        // В Java 17+ используется String.format или конкатенация, так как formatted() может требовать импорта или быть недоступен в старых версиях MC.
        return String.format("type/%s_%s", prefix, processedKey);
    }
}
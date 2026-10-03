package io.yukkuric.hexparse.parsers.str2nbt.plugins;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import io.yukkuric.hexparse.config.HexParseConfig;
import io.yukkuric.hexparse.misc.StringEscaper;
import io.yukkuric.hexparse.misc.StringProcessors;
import io.yukkuric.hexparse.misc.plugins.FairPlayPropertyNameMaker;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import miyucomics.hexcellular.PropertyIota;
import miyucomics.hexpose.iotas.IdentifierIota; // Импорт для Identifier
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.ejml.simple.SimpleMatrix;
import ram.talia.moreiotas.api.casting.iota.ItemTypeIota;
import ram.talia.moreiotas.api.casting.iota.MatrixIota;


import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Prefix;
import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Regex;

public class PluginConstParsers {

    // --- MoreIotas Parsers ---

    public static BaseConstParser TO_STRING_LIT = new Prefix("\"") {
        @Override
        public Iota parse(String node) {
            node = node.substring(1, node.length() - 1);
            var string = StringEscaper.Companion.unescape(node);
            return ram.talia.moreiotas.api.casting.iota.StringIota.make(string);
        }
    };

    // Парсер для Item Type (предмет/блок без данных)
    public static BaseConstParser TO_ITEM_TYPE = new Regex("^type/(item|block)_") {
        @Override
        public Iota parse(String node) {
            // Формат: type/item_diamond или type/block_stone
            var parts = node.split("_", 2);
            if (parts.length < 2) throw new IllegalArgumentException("Invalid item type format");

            String type = parts[0].split("/")[1]; // item или block
            String name = parts[1];

            ResourceLocation rl = ResourceLocation.parse(name.contains(":") ? name : "minecraft:" + name);

            if ("block".equals(type)) {
                Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(rl);
                return new ItemTypeIota(block);
            } else {
                Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
                return new ItemTypeIota(item);
            }
        }
    };

    // Парсер для Entity Type (если нужен, аналогично Item Type)
    public static BaseConstParser TO_ENTITY_TYPE = new Regex("^type/entity_") {
        @Override
        public Iota parse(String node) {
            // Реализация зависит от того, есть ли EntityTypeIota в MoreIotas
            // Пока оставим заглушку или простую реализацию, если класса нет
            throw new UnsupportedOperationException("Entity Type parsing not fully implemented yet");
        }
    };

    // Парсер для Iota Type (строковый идентификатор типа иоты)
    public static BaseConstParser TO_IOTA_TYPE = new Regex("^type_iota_") {
        @Override
        public Iota parse(String node) {
            // Реализация зависит от наличия IotaTypeIota в MoreIotas
            throw new UnsupportedOperationException("Iota Type parsing not fully implemented yet");
        }
    };

    // Парсер для матриц MoreIotas
    public static BaseConstParser TO_MATRIX = new Regex("^mat_") {
        @Override
        public Iota parse(String node) {
            // Формат: mat_rows_cols_val1_val2...
            var raw = node.substring(4).split("_");
            if (raw.length < 2) throw new IllegalArgumentException("Invalid matrix format");

            int rows = Integer.parseInt(raw[0]);
            int cols = Integer.parseInt(raw[1]);

            double[] data = new double[rows * cols];
            for (int i = 0; i < data.length; i++) {
                if (i + 2 >= raw.length) throw new IllegalArgumentException("Not enough data for matrix");
                data[i] = Double.parseDouble(raw[i + 2]);
            }

            SimpleMatrix matrix = new SimpleMatrix(rows, cols, true, data);
            return new MatrixIota(matrix);
        }
    };


    public static BaseConstParser TO_IDENTIFIER = new Regex("^id_") {
        @Override
        public Iota parse(String node) {
            var idStr = node.substring(3);
            ResourceLocation rl;
            if (idStr.contains(":")) {
                rl = ResourceLocation.parse(idStr);
            } else {
                rl = ResourceLocation.fromNamespaceAndPath("minecraft", idStr);
            }
            return new IdentifierIota(rl);
        }
    };


    public static class ToProperty extends Regex {
        protected ToProperty(String regex) {
            super(regex);
        }
        String getPropValue(String str) {
            str = StringProcessors.APPEND_UNDERLINE.apply(str);
            if (HexParseConfig.fairPlayPropNames()) {
                str = FairPlayPropertyNameMaker.generatePropertyName(str);
                str = StringProcessors.APPEND_UNDERLINE.apply(str);
            }
            return str;
        }
        @Override
        public Iota parse(String node) {
            var str = node.substring(node.indexOf('_') + 1);
            return new PropertyIota(getPropValue(str), false);
        }
        @Override
        public int getCost() {
            return super.getCost() + (int) MediaConstants.CRYSTAL_UNIT;
        }

        public static class Private extends ToProperty implements IPlayerBinder {
            private ServerPlayer owner;

            protected Private(String regex) {
                super(regex);
            }
            @Override
            public void BindPlayer(ServerPlayer p) {
                owner = p;
            }
            @Override
            String getPropValue(String str) {
                if (HexParseConfig.fairPlayPropNames()) str = FairPlayPropertyNameMaker.generatePropertyName(str);
                return "[face:%s]@%s".formatted(owner.getScoreboardName(), str);
            }
        }
    }

    public static BaseConstParser TO_PROPERTY = new ToProperty("^prop(erty)?_");
    public static BaseConstParser TO_MY_PROPERTY = new ToProperty.Private("^myprop_");

    // Заглушки для отсутствующих парсеров, чтобы код компилировался
    public static BaseConstParser TO_STRING = new Prefix("str_") {
        @Override
        public Iota parse(String node) {
            return ram.talia.moreiotas.api.casting.iota.StringIota.make(node.substring(4));
        }
    };

    public static BaseConstParser TO_BLOCK_TYPE = TO_ITEM_TYPE; // Можно использовать тот же парсер
}
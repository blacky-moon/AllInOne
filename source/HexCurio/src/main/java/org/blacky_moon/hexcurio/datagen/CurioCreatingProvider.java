package org.blacky_moon.hexcurio.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.blacky_moon.hexcurio.HexCurio;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class CurioCreatingProvider extends CuriosDataProvider {

    public CurioCreatingProvider(String modId, PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries) {
        super(modId, output, fileHelper, registries);
    }

    @Override
    public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
        createSlot("head_part_hex").size(1).icon(ResourceLocation.fromNamespaceAndPath(HexCurio.MOD_ID,"slot/head_part_hex"));
        createSlot("body_part_hex").size(1);
        createSlot("hands_part_hex").size(1);
        createSlot("legs_part_hex").size(1);
        createSlot("feet_part_hex").size(1);
        createSlot("back_part_hex").size(1);
    }
}
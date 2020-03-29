package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.item.KevlarItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;

public class AlfalfaBlock extends CropBlock {


    protected AlfalfaBlock(Settings settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return KevlarItems.ALFALFA_SEEDS;
    }


}

package com.notvanilla.kevlar.block;

import com.notvanilla.kevlar.item.KevlarItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.StringIdentifiable;

public enum FeedType implements StringIdentifiable {

    WHEAT(Items.WHEAT, "wheat"), ALFALFA(KevlarItems.ALFALFA, "alfalfa"), EMPTY(null, "empty");

    private final Item crop;
    private final String name;
    FeedType(Item item, String name) {
        crop =  item;
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Item getCrop() {
        return this.crop;
    }
}

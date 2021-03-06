package com.notvanilla.kevlar.block;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;

public class KevlarProperties {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);
    public static final EnumProperty<FeedType> FEED_TYPE = EnumProperty.of("feed_type", FeedType.class);

}

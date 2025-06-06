package com.wanderersoftherift.wotr.item.essence;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Data Map capturing the essence values of an item
 *
 * @param values A mapping of essence type to value for the item
 */
public record EssenceValue(Object2IntMap<ResourceLocation> values) {
    public static final Codec<EssenceValue> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
            .xmap(EssenceValue::new, EssenceValue::values);

    public static final String ESSENCE_TYPE_PREFIX = "essence_type";

    /**
     * Single type/value constructor
     */
    public EssenceValue(ResourceLocation type, int value) {
        this(Object2IntMaps
                .unmodifiable(new Object2IntArrayMap<>(new ResourceLocation[] { type }, new int[] { value })));
    }

    public EssenceValue(ResourceLocation type1, int value1, ResourceLocation type2, int value2) {
        this(Object2IntMaps.unmodifiable(
                new Object2IntArrayMap<>(new ResourceLocation[] { type1, type2 }, new int[] { value1, value2 })));
    }

    public EssenceValue(ResourceLocation type1, int value1, ResourceLocation type2, int value2, ResourceLocation type3,
            int value3) {
        this(Object2IntMaps.unmodifiable(new Object2IntArrayMap<>(new ResourceLocation[] { type1, type2, type3 },
                new int[] { value1, value2, value3 })));
    }

    /**
     * Map constructor
     */
    public EssenceValue(Map<ResourceLocation, Integer> values) {
        this(Object2IntMaps.unmodifiable(new Object2IntArrayMap<>(values)));
    }

}

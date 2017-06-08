package org.blockgaming.mgcore.util;

import me.koenn.kingdomwars.deployables.DeployableBlock;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jnbt.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class NBTUtil {

    public static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Deployable file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }

    public static DeployableBlock getBlock(CompoundTag tag) {
        Vector offset = parseOffset(getChildTag(tag.getValue(), "offset", ListTag.class));
        byte data = getChildTag(tag.getValue(), "data", ByteTag.class).getValue();
        Material type = Material.valueOf(getChildTag(tag.getValue(), "type", StringTag.class).getValue());

        return new DeployableBlock(type, data, offset);
    }

    private static Vector parseOffset(ListTag offsetTag) {
        try {
            List<Tag> offsets = offsetTag.getValue();
            return new Vector(((DoubleTag) offsets.get(0)).getValue(), ((DoubleTag) offsets.get(1)).getValue(), ((DoubleTag) offsets.get(2)).getValue());
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Offset ListTag does not contain 3 elements.");
        }
    }
}

package org.blockgaming.mgcore.util;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public final class DeployableHelper {

    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    public static BlockFace getPlayerDirection(Player player) {
        return yawToFace(player.getLocation().getYaw());
    }

    public static Vector rotateOffsetTowards(Vector offset, BlockFace towards) {
        int index = Arrays.asList(axis).indexOf(towards);
        if (index == 0) {
            return offset;
        }
        return rotate(offset, index);
    }

    private static Vector rotate(Vector vector, int times) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        vector = rotate(x, y, z);
        times--;
        if (times > 0) {
            vector = rotate(vector, times);
        }
        return vector;
    }

    private static Vector rotate(double x, double y, double z) {
        double newX, newZ;
        newZ = x;
        newX = z == 0 ? 0 : -z;
        return new Vector(newX, y, newZ);
    }

    private static BlockFace yawToFace(float yaw) {
        return axis[Math.round(yaw / 90f) & 0x3];
    }
}

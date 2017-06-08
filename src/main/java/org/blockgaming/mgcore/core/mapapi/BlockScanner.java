package org.blockgaming.mgcore.core.mapapi;

import org.blockgaming.mgcore.MGCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Copyright (C) BlockGaming - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public final class BlockScanner {

    private static final Material[] colorMaterials = new Material[]{Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE};
    private static final short[] colorIds = new short[]{14, 11};

    @SuppressWarnings("deprecation")
    public static void findColoredBlocks(final Location pos1, final Location pos2, final Callback callback) {
        final boolean xMove = pos1.getX() < pos2.getX();
        final boolean yMove = pos1.getY() < pos2.getY();
        final boolean zMove = pos1.getZ() < pos2.getZ();
        final Vector direction = new Vector(xMove ? 1 : -1, yMove ? 1 : -1, zMove ? 1 : -1);

        Bukkit.getScheduler().scheduleAsyncDelayedTask(MGCore.getInstance(), () -> {
            List<Location> coloredBlocks = new ArrayList<>();
            for (int x = (int) pos1.getX(); xMove ? x <= pos2.getX() : x >= pos2.getX(); x += direction.getX()) {
                for (int y = (int) pos1.getY(); yMove ? y <= pos2.getY() : y >= pos2.getY(); y += direction.getY()) {
                    for (int z = (int) pos1.getZ(); zMove ? z <= pos2.getZ() : z >= pos2.getZ(); z += direction.getZ()) {
                        final Location location = new Location(pos1.getWorld(), x, y + 0.5, z);
                        final Material type = location.getBlock().getType();
                        final short colorId = location.getBlock().getData();

                        for (Material material : colorMaterials) {
                            if (material.equals(type)) {
                                for (short id : colorIds) {
                                    if (colorId == id) {
                                        coloredBlocks.add(location);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(MGCore.getInstance(), () -> callback.done(coloredBlocks));
        });
    }

    public interface Callback {

        void done(List<Location> coloredBlocks);
    }
}

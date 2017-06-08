package org.blockgaming.mgcore.core.mapapi;

import me.koenn.core.data.JSONManager;
import me.koenn.core.misc.LocationHelper;
import org.blockgaming.mgcore.MGCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class MapLoader {

    public static void loadMap(String file) {
        JSONManager manager = new JSONManager(MGCore.getInstance(), file);
        String name = (String) manager.getFromBody("name");

        JSONObject blueSpawn = (JSONObject) manager.getFromBody("blueSpawn");
        JSONObject redSpawn = (JSONObject) manager.getFromBody("redSpawn");

        Location blue = new Location(Bukkit.getWorld((String) blueSpawn.get("world")), (double) blueSpawn.get("x") + 0.5, (double) blueSpawn.get("y") + 0.5, (double) blueSpawn.get("z") + 0.5);
        Location red = new Location(Bukkit.getWorld((String) redSpawn.get("world")), (double) redSpawn.get("x") + 0.5, (double) redSpawn.get("y") + 0.5, (double) redSpawn.get("z") + 0.5);

        double blueDoorX = (double) blueSpawn.get("doorx") + 0.5;
        double redDoorX = (double) redSpawn.get("doorx") + 0.5;
        double blueDoorZ = (double) blueSpawn.get("doorz") + 0.5;
        double redDoorZ = (double) redSpawn.get("doorz") + 0.5;

        JSONArray redPoint = (JSONArray) redSpawn.get("capturePoint");
        JSONArray bluePoint = (JSONArray) blueSpawn.get("capturePoint");
        Location[] redPointCorners = new Location[4];
        Location[] bluePointCorners = new Location[4];

        for (int i = 0; i < redPoint.size(); i++) {
            JSONObject redCorner = (JSONObject) redPoint.get(i);
            redPointCorners[i] = new Location(Bukkit.getWorld((String) redSpawn.get("world")), (double) redCorner.get("x") + 0.5, (double) redCorner.get("y") + 0.5, (double) redCorner.get("z") + 0.5);

            JSONObject blueCorner = (JSONObject) bluePoint.get(i);
            bluePointCorners[i] = new Location(Bukkit.getWorld((String) redSpawn.get("world")), (double) blueCorner.get("x") + 0.5, (double) blueCorner.get("y") + 0.5, (double) blueCorner.get("z") + 0.5);
        }

        JSONArray coloredBlocks = (JSONArray) manager.getFromBody("coloredBlocks");
        Location[] coloredLocations = new Location[coloredBlocks.size()];
        for (int i = 0; i < coloredLocations.length; i++) {
            coloredLocations[i] = LocationHelper.fromString((String) coloredBlocks.get(i));
        }

        JSONObject properties = (JSONObject) manager.getFromBody("properties");

        Map.maps.register(new Map(name, blue, red, blueDoorX, blueDoorZ, redDoorX, redDoorZ, redPointCorners, bluePointCorners, coloredLocations, properties));
    }
}

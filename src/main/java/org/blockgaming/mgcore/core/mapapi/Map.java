package org.blockgaming.mgcore.core.mapapi;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.core.registry.Registry;
import me.koenn.fakeblockapi.FakeBlock;
import me.koenn.fakeblockapi.FakeBlockAPI;
import org.blockgaming.mgcore.util.Door;
import org.blockgaming.mgcore.games.kingdomwars.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Map {

    public static Registry<Map> maps = new Registry<>(Map::getName);

    private final String name;
    private final Location[] spawns = new Location[2];
    private final Door[] doors = new Door[2];
    private final Location[][] controlPointCorners = new Location[2][];
    private final ControlPoint[] controlPoints = new ControlPoint[2];
    private final Location[] coloredBlocks;
    private final HashMap<String, Object> properties;
    private int renderTask;

    public Map(String name, Location blueSpawn, Location redSpawn, double blueXDoor, double blueZDoor, double redXDoor, double redZDoor, Location[] redControlPoint, Location[] blueControlPoint, Location[] coloredBlocks, HashMap<String, Object> properties) {
        //TODO: Could use some cleaning up.

        this.name = name;

        this.spawns[Team.BLUE.getIndex()] = blueSpawn;
        this.spawns[Team.RED.getIndex()] = redSpawn;

        boolean blueZ = blueXDoor == Double.MAX_VALUE;
        this.doors[Team.BLUE.getIndex()] = new Door(blueZ ? blueZDoor : blueXDoor, blueZ ? Door.DoorType.Z : Door.DoorType.X);
        boolean redZ = redXDoor == Double.MAX_VALUE;
        this.doors[Team.RED.getIndex()] = new Door(redZ ? redZDoor : redXDoor, redZ ? Door.DoorType.Z : Door.DoorType.X);

        this.controlPointCorners[Team.BLUE.getIndex()] = blueControlPoint;
        this.controlPointCorners[Team.RED.getIndex()] = redControlPoint;

        this.controlPoints[Team.BLUE.getIndex()] = new ControlPoint(blueControlPoint, Team.BLUE);
        this.controlPoints[Team.RED.getIndex()] = new ControlPoint(redControlPoint, Team.RED);

        this.coloredBlocks = coloredBlocks;

        this.properties = properties;
    }

    public static Map getMap(String name) {
        return maps.get(name);
    }

    public void reset() {
        for (ControlPoint controlPoint : this.controlPoints) {
            controlPoint.forceReset();
        }
    }

    @SuppressWarnings("deprecation")
    public void load(Game game) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(KingdomWars.getInstance(), () -> {
            Bukkit.getLogger().info("Starting packet send...");
            long time = System.currentTimeMillis();
            int packets = 0;
            for (Location location : this.coloredBlocks) {
                final Block block = location.getBlock();
                final short meta = block.getData();
                final List<Player> team = game.getTeam(Team.RED);
                FakeBlockAPI.fakeBlockRegistry.register(new FakeBlock(location, block.getType(), (short) (meta == 11 ? 14 : 11), team.toArray(new Player[team.size()])));
                packets++;
            }
            Bukkit.getLogger().info("Send out " + packets + " packets!");
            long taken = System.currentTimeMillis() - time;
            Bukkit.getLogger().info("Taken " + taken + "ms");
        });
    }

    public void renderCapture(Team team) {
        //TODO: Idk if this looks good enough...
        for (Location location : this.controlPointCorners[team.getIndex()]) {
            CloudEffect effect = new CloudEffect(new EffectManager(KingdomWars.getInstance()));
            DynamicLocation dynamicLocation = new DynamicLocation(location);
            effect.cloudParticle = ParticleEffect.CLOUD;
            effect.mainParticle = ParticleEffect.CLOUD;
            effect.setDynamicOrigin(dynamicLocation);
            effect.setDynamicTarget(dynamicLocation);
            effect.iterations = 5;
            effect.start();
        }
    }

    public void startRendering(final Game game) {
        this.renderTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), () -> {
            for (int i = 0; i <= 1; i++) {
                boolean red = i == Team.RED.getIndex();
                ParticleRenderer.renderLine(this.controlPointCorners[i][0], this.controlPointCorners[i][1], red, game);
                ParticleRenderer.renderLine(this.controlPointCorners[i][1], this.controlPointCorners[i][2], red, game);
                ParticleRenderer.renderLine(this.controlPointCorners[i][2], this.controlPointCorners[i][3], red, game);
                ParticleRenderer.renderLine(this.controlPointCorners[i][3], this.controlPointCorners[i][0], red, game);
            }
        }, 0, 2);
    }

    public void stopRendering() {
        Bukkit.getScheduler().cancelTask(this.renderTask);
    }

    public Location getSpawn(Team team) {
        return this.spawns[team.getIndex()];
    }

    public Door getDoor(Team team) {
        return this.doors[team.getIndex()];
    }

    public Location[] getControlpointCorners(Team team) {
        return this.controlPointCorners[team.getIndex()];
    }

    public ControlPoint[] getControlPoints() {
        return this.controlPoints;
    }

    public String getName() {
        return this.name;
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }
}

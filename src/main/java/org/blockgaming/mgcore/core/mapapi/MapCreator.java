package org.blockgaming.mgcore.core.mapapi;

import me.koenn.core.gui.Gui;
import me.koenn.core.keyboard.KeyboardGui;
import me.koenn.core.misc.LocationHelper;
import org.blockgaming.mgcore.MGCore;
import org.blockgaming.mgcore.util.Messager;
import org.blockgaming.mgcore.util.References;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class MapCreator implements Listener {

    public static MapCreator instance;
    private final HashMap<Player, JSONObject> tmpMapFiles = new HashMap<>();
    private final HashMap<Player, Location> tmpLocation = new HashMap<>();

    public MapCreator() {
        instance = this;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().isOp()) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.WOOD_HOE) {
            return;
        }

        ItemMeta meta = player.getItemInHand().getItemMeta();
        if (!meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) || !meta.hasLore()) {
            return;
        }

        ToolMode mode = getToolMode(player.getItemInHand());
        if (player.isSneaking() && event.getClickedBlock() == null) {
            if (event.getAction() == Action.LEFT_CLICK_AIR) {
                KeyboardGui gui = new KeyboardGui(player, "Map Name", input -> {
                    MapSaveGui saveGui = new MapSaveGui(player, input, tmpMapFiles.get(player));
                    Gui.registerGui(saveGui, MGCore.getInstance());
                    saveGui.open();
                });
                Gui.registerGui(gui, MGCore.getInstance());
                gui.open();
            } else {
                ToolMode next = getNextMode(mode);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "Mode: " + next.name());
                meta.setLore(lore);
                player.getItemInHand().setItemMeta(meta);
                Messager.playerMessage(player, References.MODE_CHANGE.replace("%mode%", next.name()));
            }
        } else if (event.getClickedBlock() != null) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
                return;
            }

            event.setCancelled(true);
            JSONObject tmpMapFile;
            if (!tmpMapFiles.containsKey(player)) {
                tmpMapFile = writeBaseMapFile(new JSONObject());
                tmpMapFiles.put(player, tmpMapFile);
            } else {
                tmpMapFile = tmpMapFiles.get(player);
            }

            Location clicked = event.getClickedBlock().getLocation();
            JSONObject blue = (JSONObject) tmpMapFile.get("blueSpawn");
            JSONObject red = (JSONObject) tmpMapFile.get("redSpawn");

            switch (mode) {
                case Spawn:
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        writeSpawnCoords(clicked, blue);
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        writeSpawnCoords(clicked, red);
                    }
                    Messager.playerMessage(player, References.SET_SPAWN.replace("%team%", event.getAction() == Action.LEFT_CLICK_BLOCK ? "Blue" : "Red").replace("%coords%", LocationHelper.getString(clicked)));
                    break;
                case Door:
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        writeDoorCoords(clicked, blue, !player.isSneaking());
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        writeDoorCoords(clicked, red, !player.isSneaking());
                    }
                    Messager.playerMessage(player, References.SET_DOOR.replace("%team%", event.getAction() == Action.LEFT_CLICK_BLOCK ? "Blue" : "Red").replace("%coords%", LocationHelper.getString(clicked)));
                    break;
                case Point:
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        blue.put("capturePoint", addPointCorner(clicked, (JSONArray) blue.get("capturePoint")));
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        red.put("capturePoint", addPointCorner(clicked, (JSONArray) red.get("capturePoint")));
                    }
                    Messager.playerMessage(player, References.ADD_CORNER.replace("%team%", event.getAction() == Action.LEFT_CLICK_BLOCK ? "Blue" : "Red").replace("%coords%", LocationHelper.getString(clicked)));
                    break;
                case Color:
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        tmpLocation.put(player, clicked);
                        Messager.playerMessage(player, References.SET_POS1.replace("%coords%", LocationHelper.getString(clicked)));
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        BlockScanner.findColoredBlocks(tmpLocation.get(player), clicked, (blocks) -> {
                            Location[] coloredBlocks = blocks.toArray(new Location[blocks.size()]);
                            JSONArray coloredBlocksJson = new JSONArray();
                            for (Location location : coloredBlocks) {
                                coloredBlocksJson.add(LocationHelper.getString(location));
                            }
                            tmpMapFile.put("coloredBlocks", coloredBlocksJson);
                            Bukkit.getLogger().info(tmpMapFile.toJSONString());
                        });
                        Messager.playerMessage(player, References.LOADED_BLOCKS);
                    }
            }

            tmpMapFile.put("blueSpawn", blue);
            tmpMapFile.put("redSpawn", red);
            Bukkit.getLogger().info(tmpMapFile.toJSONString());
        }
    }

    private ToolMode getToolMode(ItemStack stack) {
        return ToolMode.valueOf(stack.getItemMeta().getLore().get(0).split(" ")[1]);
    }

    private ToolMode getNextMode(ToolMode mode) {
        switch (mode) {
            case Spawn:
                return ToolMode.Door;
            case Door:
                return ToolMode.Point;
            case Point:
                return ToolMode.Color;
            case Color:
                return ToolMode.Spawn;
            default:
                return ToolMode.Spawn;
        }
    }

    private JSONObject writeBaseMapFile(JSONObject emptyMapFile) {
        JSONObject blueBase = new JSONObject();
        JSONObject redBase = new JSONObject();
        blueBase.put("capturePoint", new JSONArray());
        redBase.put("capturePoint", new JSONArray());
        emptyMapFile.put("blueSpawn", blueBase);
        emptyMapFile.put("redSpawn", redBase);
        return emptyMapFile;
    }

    private JSONArray addPointCorner(Location location, JSONArray point) {
        JSONObject coord = new JSONObject();
        coord.put("x", location.getX());
        coord.put("y", location.getY());
        coord.put("z", location.getZ());
        point.add(coord);
        return point;
    }

    private void writeSpawnCoords(Location spawn, JSONObject spawnJson) {
        spawnJson.put("world", spawn.getWorld().getName());
        spawnJson.put("x", spawn.getX());
        spawnJson.put("y", spawn.getY());
        spawnJson.put("z", spawn.getZ());
    }

    private void writeDoorCoords(Location coords, JSONObject spawnJson, boolean x) {
        spawnJson.put("doorx", x ? coords.getX() : 10000.0);
        spawnJson.put("doorz", x ? 10000.0 : coords.getZ());
    }

    public void resetPlayerMapFile(Player player) {
        tmpMapFiles.remove(player);
    }

    private enum ToolMode {
        Spawn, Door, Point, Color
    }
}

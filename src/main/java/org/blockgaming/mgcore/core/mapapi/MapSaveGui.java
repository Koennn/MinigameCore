package org.blockgaming.mgcore.core.mapapi;

import me.koenn.core.data.JSONManager;
import me.koenn.core.gui.Gui;
import me.koenn.core.gui.Option;
import me.koenn.core.keyboard.KeyboardGui;
import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import org.blockgaming.mgcore.MGCore;
import org.blockgaming.mgcore.util.Messager;
import org.blockgaming.mgcore.util.References;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class MapSaveGui extends Gui {

    private String name;
    private HashMap<String, Object> customProperties = new HashMap<>();

    public MapSaveGui(Player player, String name, JSONObject tmpMapFile) {
        super(player, "Save Map: " + new FancyString(name).toString(), 9);
        this.name = name;

        this.addOption(new Option(ItemHelper.makeItemStack(Material.PAPER, 1, (short) 0, "Change Name", null), () -> {
            KeyboardGui gui = new KeyboardGui(player, "Map Name", input -> {
                this.name = input;
                Gui.registerGui(this, MGCore.getInstance());
                this.open();
            });
            Gui.registerGui(gui, MGCore.getInstance());
            gui.open();
        }));

        this.addOption(new Option(ItemHelper.makeItemStack(Material.BOOK_AND_QUILL, 1, (short) 0, "Add Custom Properties", null), () -> {
            KeyboardGui keyGui = new KeyboardGui(player, "Property Key", key -> {
                KeyboardGui valueGui = new KeyboardGui(player, "Property Value", value -> {
                    customProperties.put(key, value);
                    Gui.registerGui(this, MGCore.getInstance());
                    this.open();
                }, false);
                Gui.registerGui(valueGui, MGCore.getInstance());
                valueGui.open();
            }, false);
            Gui.registerGui(keyGui, MGCore.getInstance());
            keyGui.open();
        }));

        this.setOption(8, new Option(ItemHelper.makeItemStack(Material.WOOL, 1, (short) 5, "Save Map", LoreHelper.makeLore(ChatColor.DARK_RED + "WARNING, THIS CANNOT BE UNDONE!")), () -> {
            String fileName = this.name.toLowerCase().replace(" ", "_");
            File file = new File(MGCore.getInstance().getDataFolder(), fileName + "_map.json");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONManager manager = new JSONManager(MGCore.getInstance(), fileName + "_map.json");
            try {
                manager.setInBody("name", new FancyString(this.name).toString());
                manager.setInBody("blueSpawn", tmpMapFile.get("blueSpawn"));
                manager.setInBody("redSpawn", tmpMapFile.get("redSpawn"));
                manager.setInBody("properties", new JSONObject(customProperties));
                manager.setInBody("coloredBlocks", tmpMapFile.get("coloredBlocks"));
                manager.saveBodyToFile();
            } catch (Exception ex) {
                Messager.playerMessage(player, References.SAVE_ERROR);
                player.closeInventory();
                MapCreator.instance.resetPlayerMapFile(player);
                return;
            }
            Messager.playerMessage(player, References.SAVE_SUCCESSFUL);
            player.closeInventory();
        }));
    }
}

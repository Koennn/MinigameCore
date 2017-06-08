package org.blockgaming.mgcore.util;

import me.koenn.core.misc.ReflectionHelper;
import me.koenn.core.player.CPlayerRegistry;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.classes.Kit;
import org.blockgaming.mgcore.games.kingdomwars.Team;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class PlayerHelper {

    public static Game getGame(Player player) {
        for (Game game : Game.gameRegistry) {
            for (Player gamePlayer : game.getPlayers()) {
                if (gamePlayer.getUniqueId().equals(player.getUniqueId())) {
                    return game;
                }
            }
        }
        return null;
    }

    public static Team getTeam(Player player) {
        Game game = getGame(player);
        if (game == null) {
            return null;
        }
        return game.getTeam(Team.BLUE).contains(player) ? Team.BLUE : Team.RED;
    }

    public static boolean canDamage(Player player1, Player player2) {
        return getTeam(player1) != getTeam(player2);
    }

    public static boolean isInGame(Player player) {
        return getGame(player) != null;
    }

    public static Class getMostPreferredClass(Player player) {
        if (player.getClass().getSimpleName().contains("TestPlayer")) {
            return ClassLoader.getClass((String) ReflectionHelper.callMethod(player, "getPreferredClass"));
        }
        return ClassLoader.getClass(CPlayerRegistry.getCPlayer(player.getUniqueId()).get("most-preferred-class"));
    }

    public static Class getLeastPreferredClass(Player player) {
        if (player.getClass().getSimpleName().contains("TestPlayer")) {
            return ClassLoader.getClass((String) ReflectionHelper.callMethod(player, "getLeastPreferredClass"));
        }
        return ClassLoader.getClass(CPlayerRegistry.getCPlayer(player.getUniqueId()).get("least-preferred-class"));
    }

    public static void giveKit(Player player, Kit kit) {
        for (ItemStack item : kit.getItems()) {
            player.getInventory().addItem(item);
        }
    }

    public static String[] usernameArray(List<Player> playerList) {
        String[] players = new String[playerList.size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = playerList.get(i).getName();
        }
        return players;
    }
}

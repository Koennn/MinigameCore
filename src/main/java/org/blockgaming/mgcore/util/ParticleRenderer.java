package org.blockgaming.mgcore.util;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.effect.TeamLineEffect;
import me.koenn.kingdomwars.game.Game;
import org.blockgaming.mgcore.games.kingdomwars.Team;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class ParticleRenderer {

    public static void renderLine(Location pos1, Location pos2, boolean red, Game game) {
        List<Player> blueTeam = game.getTeam(Team.BLUE);
        List<Player> redTeam = game.getTeam(Team.RED);

        createLineEffect(red ? blueTeam : redTeam, ParticleEffect.DRIP_LAVA, pos1, pos2);
        createLineEffect(red ? redTeam : blueTeam, ParticleEffect.DRIP_WATER, pos1, pos2);
    }

    private static void createLineEffect(List<Player> team, ParticleEffect particleEffect, Location pos1, Location pos2) {
        TeamLineEffect lineEffect = new TeamLineEffect(new EffectManager(KingdomWars.getInstance()), team);
        lineEffect.particle = particleEffect;
        lineEffect.setDynamicOrigin(new DynamicLocation(pos1));
        lineEffect.setDynamicTarget(new DynamicLocation(pos2));
        lineEffect.iterations = 1;
        lineEffect.particles = 40;
        lineEffect.duration = 2;
        lineEffect.color = Color.BLUE;
        lineEffect.start();
    }
}

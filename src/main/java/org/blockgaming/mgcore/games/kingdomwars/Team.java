package org.blockgaming.mgcore.games.kingdomwars;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public enum Team {

    BLUE(0), RED(1);

    int index;

    Team(int index) {
        this.index = index;
    }

    public static Team getTeam(int index) {
        for (Team team : values()) {
            if (team.index == index) {
                return team;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public Team getOpponent() {
        return this == RED ? BLUE : RED;
    }
}

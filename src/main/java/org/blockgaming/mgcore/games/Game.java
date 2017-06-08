package org.blockgaming.mgcore.games;

import org.blockgaming.mgcore.core.mapapi.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) BlockGaming - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public class Game {

    private Map map;
    private GamePhase phase;
    private List<UUID> players = new ArrayList<>();
    private List<UUID> spectators = new ArrayList<>();

    public Game() {

    }
}

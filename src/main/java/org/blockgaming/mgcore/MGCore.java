package org.blockgaming.mgcore;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * MinigameCore main class.
 * <p>
 * Copyright (C) BlockGaming - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, June 2017
 */
public final class MGCore extends JavaPlugin {

    private static MGCore instance;

    public static MGCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }
}

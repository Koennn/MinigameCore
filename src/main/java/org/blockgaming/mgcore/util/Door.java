package org.blockgaming.mgcore.util;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class Door {

    private final double location;
    private final DoorType type;

    public Door(double location, DoorType type) {
        this.location = location;
        this.type = type;
    }

    public double getLocation() {
        return location;
    }

    public DoorType getType() {
        return type;
    }

    public enum DoorType {
        X, Z
    }
}

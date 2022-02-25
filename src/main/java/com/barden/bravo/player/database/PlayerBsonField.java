package com.barden.bravo.player.database;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * User bson fields.
 */
public enum PlayerBsonField {
    ID("id"),
    NAME("name"),
    INVENTORY("inventory"),
    CURRENCIES("currencies"),
    STATS("stats"),
    SETTINGS("settings"),
    STATISTICS("statistics");

    private final String path;

    PlayerBsonField(@Nonnull String path) {
        this.path = Objects.requireNonNull(path);
    }

    /**
     * Gets bson field path.
     *
     * @return Bson field path.
     */
    @Nonnull
    public String getPath() {
        return this.path;
    }
}
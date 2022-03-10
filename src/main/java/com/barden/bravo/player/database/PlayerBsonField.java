package com.barden.bravo.player.database;

import com.barden.bravo.database.DatabaseField;
import com.barden.bravo.player.Player;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Player bson fields.
 */
public enum PlayerBsonField implements DatabaseField<Player> {
    ID("id", true),
    NAME("name"),
    INVENTORY("inventory"),
    CURRENCIES("currencies"),
    STATS("stats"),
    SETTINGS("settings"),
    STATISTICS("statistics");

    private final String path;
    private final boolean query;

    /**
     * Creates a player bson field.
     *
     * @param path Player bson field path.
     */
    PlayerBsonField(@Nonnull String path) {
        this(path, false);
    }

    /**
     * Creates a player bson field.
     *
     * @param path  Player bson field path.
     * @param query If it is query or not.
     */
    PlayerBsonField(@Nonnull String path, boolean query) {
        this.path = Objects.requireNonNull(path);
        this.query = query;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    public String getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isQuery() {
        return this.query;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public BsonValue toBsonValue(@NotNull Player player) {
        return switch (this) {
            case ID -> new BsonInt64(player.getId());
            case NAME -> new BsonString(player.getName());
            case INVENTORY -> player.getInventory().toBsonDocument();
            case CURRENCIES -> player.getCurrencies().toBsonDocument();
            case STATS -> player.getStats().toBsonDocument();
            case SETTINGS -> player.getSettings().toBsonDocument();
            case STATISTICS -> player.getStatistics().toBsonDocument();
        };
    }
}
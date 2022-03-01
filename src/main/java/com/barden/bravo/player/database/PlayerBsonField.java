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
    ID("id"),
    NAME("name"),
    INVENTORY("inventory"),
    CURRENCIES("currencies"),
    STATS("stats"),
    SETTINGS("settings"),
    STATISTICS("statistics");

    private final String path;

    /**
     * Creates a player bson field.
     *
     * @param path Player bson field path.
     */
    PlayerBsonField(@Nonnull String path) {
        this.path = Objects.requireNonNull(path);
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
        return false;
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
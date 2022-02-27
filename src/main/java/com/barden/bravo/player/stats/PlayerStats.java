package com.barden.bravo.player.stats;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.stats.type.PlayerStatType;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;
import org.bson.BsonDouble;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Player stats class.
 */
public final class PlayerStats {

    private final Player player;
    private final HashMap<PlayerStatType, Double> content = new HashMap<>();

    /**
     * Creates a player stats.
     *
     * @param player Player.
     */
    public PlayerStats(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates a player stats.
     *
     * @param player   Player.
     * @param document Player stats document.
     */
    public PlayerStats(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player types bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares player stats from the declared bson document.
        document.forEach((key, value) -> this.content.put(PlayerStatType.valueOf(key), Math.max(value.asDouble().getValue(), 0.0d)));
    }

    /**
     * Gets player.
     *
     * @return Player.
     */
    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets player stat value.
     *
     * @param type Player stat type.
     * @return Player stat value.
     */
    public double get(@Nonnull PlayerStatType type) {
        return this.content.getOrDefault(Objects.requireNonNull(type, "player stat type cannot be null!"), 0.0d);
    }

    /**
     * Sets player stat value.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void set(@Nonnull PlayerStatType type, double value) {
        assert value >= 0 : "player stat value must be positive!";
        this.content.put(Objects.requireNonNull(type, "player stat type cannot be null!"), value);
    }

    /**
     * Adds value to player stat.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull PlayerStatType type, double value) {
        assert value >= 0 : "player stat value must be positive!";
        this.content.put(Objects.requireNonNull(type, "player stat type cannot be null!"), this.get(type) + value);
    }

    /**
     * Removes value from player stat.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull PlayerStatType type, double value) {
        assert value >= 0 : "player stat value must be positive!";
        this.content.put(Objects.requireNonNull(type, "player stat type cannot be null!"), Math.max(this.get(type) - value, 0));
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player stats to a json object.
     *
     * @return Player stats json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        this.content.forEach((stat, value) -> json.addProperty(stat.name(), value));
        return json;
    }

    /**
     * Converts player stats to a bson document.
     *
     * @return Player stats bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        this.content.forEach((stat, value) -> document.put(stat.name(), new BsonDouble(value)));
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player stats.
     *
     * @param json Player stats json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player stats json object cannot be null!");

        //Clears all player stats to make sure it won't have existed player stat.
        this.content.clear();

        json.entrySet().forEach((entry) -> this.content.put(PlayerStatType.valueOf(entry.getKey()), Math.max(entry.getValue().getAsDouble(), 0.0d)));
    }
}

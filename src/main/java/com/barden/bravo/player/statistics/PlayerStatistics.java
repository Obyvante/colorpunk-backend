package com.barden.bravo.player.statistics;

import com.barden.bravo.player.Player;
import com.barden.bravo.statistics.type.StatisticType;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;
import org.bson.BsonDouble;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Player statistics class.
 */
public final class PlayerStatistics {

    private final Player player;
    private final HashMap<StatisticType, Double> content = new HashMap<>();

    /**
     * Creates a player statistics.
     *
     * @param player Player.
     */
    public PlayerStatistics(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates a player statistics.
     *
     * @param player   Player.
     * @param document Player statistics document.
     */
    public PlayerStatistics(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player statistics bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares statistics from the declared bson document.
        document.forEach((key, value) -> this.content.put(StatisticType.valueOf(key), value.asDouble().getValue()));
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
     * Gets player statistic value.
     *
     * @param type Player statistic type.
     * @return Player statistic value.
     */
    public double get(@Nonnull StatisticType type) {
        return this.content.getOrDefault(Objects.requireNonNull(type, "player statistic type cannot be null!"), 0.0d);
    }

    /**
     * Sets player statistics value.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player statistic type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void set(@Nonnull StatisticType type, double value) {
        this.content.put(Objects.requireNonNull(type, "player statistic type cannot be null!"), Math.max(value, 0));
    }

    /**
     * Adds value to player statistic.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player statistic type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull StatisticType type, double value) {
        this.content.put(Objects.requireNonNull(type, "player statistic type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from player statistic.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player statistic type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull StatisticType type, double value) {
        this.content.put(Objects.requireNonNull(type, "player statistic type cannot be null!"), Math.max(this.get(type) - value, 0));
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player statistics to a json object.
     *
     * @return Player statistics json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        this.content.forEach((stat, value) -> json.addProperty(stat.name(), value));
        return json;
    }

    /**
     * Converts player statistics to a bson document.
     *
     * @return Player statistics bson document.
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
     * Updates player statistics.
     *
     * @param json Player statistics json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player statistics json object cannot be null!");

        //Clears all player statistics to make sure it won't have existed player statistic.
        this.content.clear();

        json.entrySet().forEach((entry) -> this.content.put(StatisticType.valueOf(entry.getKey()), entry.getValue().getAsDouble()));
    }
}

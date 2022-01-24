package com.barden.bravo.player.statistics;

import com.barden.bravo.player.Player;
import com.barden.bravo.statistics.enums.StatisticType;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseRepository;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Player statistics class.
 */
public final class PlayerStatistics {

    private final Player player;
    private HashMap<StatisticType, Double> statistics = new HashMap<>();

    /**
     * Creates player statistics object.
     *
     * @param player Player.
     */
    public PlayerStatistics(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player statistics object.
     *
     * @param player       Player.
     * @param bsonDocument Bson document. (MONGO)
     */
    public PlayerStatistics(@Nonnull Player player, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "statistics bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares statistics from the declared bson document.
        bsonDocument.forEach((key, value) -> this.statistics.put(StatisticType.valueOf(key), value.asDouble().getValue()));
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
     * Gets statistic value.
     *
     * @param type Player statistic type.
     * @return Statistic value.
     */
    public double get(@Nonnull StatisticType type) {
        return this.statistics.getOrDefault(Objects.requireNonNull(type, "type cannot be null!"), 0.0d);
    }

    /**
     * Adds value to statistic.
     * NOTE: Statistic value cannot be negative.
     *
     * @param type  Player statistic type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void set(@Nonnull StatisticType type, double value) {
        this.statistics.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(value, 0));
    }

    /**
     * Adds value to stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player statistic type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull StatisticType type, double value) {
        this.statistics.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player statistic type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull StatisticType type, double value) {
        this.statistics.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) - value, 0));
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player statistics as a json object.
     *
     * @return Statistics as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Adds statistics to created empty json object.
        this.statistics.forEach((stat, value) -> json_object.addProperty(stat.name(), value));

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player statistics object to document. (MONGO BSON)
     *
     * @return Statistics document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();
        //Adds statistics to created empty bson document.
        this.statistics.forEach((stat, value) -> bson_document.put(stat.name(), new BsonDouble(value)));

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player statistics object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player statistics json object cannot be null!");

        //Resets statistics.
        this.statistics = new HashMap<>();

        //Adds statistics from json object one by one to statistics object.
        json_object.entrySet().forEach((entry) -> this.statistics.put(StatisticType.valueOf(entry.getKey()), entry.getValue().getAsDouble()));
    }
}

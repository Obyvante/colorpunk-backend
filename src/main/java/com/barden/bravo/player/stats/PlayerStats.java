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
    private final HashMap<PlayerStatType, Double> stats = new HashMap<>();

    /**
     * Creates player stats object.
     *
     * @param player Player.
     */
    public PlayerStats(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player stats object.
     *
     * @param player       Player.
     * @param bsonDocument Bson document. (MONGO)
     */
    public PlayerStats(@Nonnull Player player, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "stats bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares stats from the declared bson document.
        bsonDocument.forEach((key, value) -> this.stats.put(PlayerStatType.valueOf(key), value.asDouble().getValue()));
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
     * Gets stat value.
     *
     * @param type Player stat type.
     * @return Player stat value.
     */
    public double get(@Nonnull PlayerStatType type) {
        return this.stats.getOrDefault(Objects.requireNonNull(type, "type cannot be null!"), 0.0d);
    }

    /**
     * Sets value to stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void set(@Nonnull PlayerStatType type, double value) {
        this.stats.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(value, 0));
    }

    /**
     * Adds value to stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull PlayerStatType type, double value) {
        this.stats.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull PlayerStatType type, double value) {
        this.stats.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) - value, 0));
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player stats as a json object.
     *
     * @return Player stats as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Adds stats to created empty json object.
        this.stats.forEach((stat, value) -> json_object.addProperty(stat.name(), value));

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player stats object to document. (MONGO BSON)
     *
     * @return Player stats document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();

        //Adds stats to created empty bson document.
        this.stats.forEach((stat, value) -> bson_document.put(stat.name(), new BsonDouble(value)));

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player stats object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player stats json object cannot be null!");

        //Resets stats.
        this.stats.clear();

        //Adds stats from json object one by one to stats object.
        json_object.entrySet().forEach((entry) -> this.stats.put(PlayerStatType.valueOf(entry.getKey()), entry.getValue().getAsDouble()));
    }
}

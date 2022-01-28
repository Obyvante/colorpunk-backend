package com.barden.bravo.player.currencies;

import com.barden.bravo.player.Player;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;
import org.bson.BsonInt32;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Player currencies class.
 */
public final class PlayerCurrencies {

    private final Player player;
    private final HashMap<PlayerCurrencyType, Integer> currencies = new HashMap<>();

    /**
     * Creates player currencies object.
     *
     * @param player Player.
     */
    public PlayerCurrencies(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player currencies object.
     *
     * @param player       Player.
     * @param bsonDocument Bson document. (Mongo)
     */
    public PlayerCurrencies(@Nonnull Player player, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "currencies bson document cannot be null!");
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        //Declares currencies from declared bson document.
        bsonDocument.forEach((key, value) -> this.currencies.put(PlayerCurrencyType.valueOf(key), value.asInt32().intValue()));
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
     * Gets currency value.
     *
     * @param type Player currency type.
     * @return Player currency value.
     */
    public int get(@Nonnull PlayerCurrencyType type) {
        return this.currencies.getOrDefault(Objects.requireNonNull(type, "type cannot be null!"), 0);
    }

    /**
     * Sets value to currency.
     * NOTE: PlayerCurrencyType value cannot be negative.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void set(@Nonnull PlayerCurrencyType type, int value) {
        this.currencies.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(value, 0));
    }

    /**
     * Adds value to currency.
     * NOTE: PlayerCurrencyType value cannot be negative.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull PlayerCurrencyType type, int value) {
        this.currencies.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from currency.
     * NOTE: PlayerCurrencyType value cannot be negative.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull PlayerCurrencyType type, int value) {
        this.currencies.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) - value, 0));
    }


    /*
    CONVERTS
     */

    /**
     * Gets currencies as a json object.
     *
     * @return Player currencies as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures fields.
        this.currencies.forEach((currency, value) -> json_object.addProperty(currency.name(), value));

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player currencies object to bson document.
     *
     * @return Player currencies bson document. (MONGO)
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();

        //Sets base fields.
        this.currencies.forEach((currency, value) -> bson_document.put(currency.name(), new BsonInt32(value)));

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player currencies object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player currencies json object cannot be null!");

        //Configures currencies.
        json_object.keySet().forEach(currency_string -> this.currencies.put(PlayerCurrencyType.valueOf(currency_string), json_object.get(currency_string).getAsInt()));
    }
}

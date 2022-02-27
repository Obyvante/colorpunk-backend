package com.barden.bravo.player.currencies;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.currencies.type.PlayerCurrencyType;
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
    private final HashMap<PlayerCurrencyType, Integer> content = new HashMap<>();

    /**
     * Creates a player currencies.
     *
     * @param player Player.
     */
    public PlayerCurrencies(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates a player currencies.
     *
     * @param player   Player.
     * @param document Player currencies bson document.
     */
    public PlayerCurrencies(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player currencies bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares player currencies from declared bson document.
        document.forEach((key, value) -> this.content.put(PlayerCurrencyType.valueOf(key), value.asInt32().intValue()));
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
     * Gets player currency value.
     *
     * @param type Player currency type.
     * @return Player currency value.
     */
    public int get(@Nonnull PlayerCurrencyType type) {
        return this.content.getOrDefault(Objects.requireNonNull(type, "player currency type cannot be null!"), 0);
    }

    /**
     * Sets player currency value.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void set(@Nonnull PlayerCurrencyType type, int value) {
        this.content.put(Objects.requireNonNull(type, "player currency type cannot be null!"), Math.max(value, 0));
    }

    /**
     * Adds value to player currency.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull PlayerCurrencyType type, int value) {
        this.content.put(Objects.requireNonNull(type, "player currency type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from player currency.
     * NOTE: Value cannot be negative
     * Otherwise, it'll convert to 0.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull PlayerCurrencyType type, int value) {
        this.content.put(Objects.requireNonNull(type, "player currency type cannot be null!"), Math.max(this.get(type) - value, 0));
    }


    /*
    CONVERTS
     */

    /**
     * Converts player currencies to a json object.
     *
     * @return Player currency json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json_object = new JsonObject();
        this.content.forEach((currency, value) -> json_object.addProperty(currency.name(), value));
        return json_object;
    }

    /**
     * Converts player currencies to a bson document.
     *
     * @return Player currency bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument bson_document = new BsonDocument();
        this.content.forEach((currency, value) -> bson_document.put(currency.name(), new BsonInt32(value)));
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player currencies.
     *
     * @param json player currencies json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player currencies json object cannot be null!");

        //Clears all player currencies to make sure it won't have existed player currencies.
        this.content.clear();

        json.keySet().forEach(currency_string -> this.content.put(PlayerCurrencyType.valueOf(currency_string), json.get(currency_string).getAsInt()));
    }
}

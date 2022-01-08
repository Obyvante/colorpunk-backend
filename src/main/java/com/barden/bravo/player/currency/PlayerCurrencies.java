package com.barden.bravo.player.currency;

import com.barden.bravo.currency.Currency;
import com.barden.bravo.player.Player;
import com.google.gson.JsonObject;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Player currencies class.
 */
public final class PlayerCurrencies {

    private final Player player;
    private final HashMap<Currency, Integer> currencies = new HashMap<>();

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
     * @param player   Player.
     * @param document Mongo document. (STATS BSON)
     */
    public PlayerCurrencies(@Nonnull Player player, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "currencies document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares currencies.
        document.forEach((key, value) -> this.currencies.put(Currency.valueOf(key), (int) value));
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
    public int get(@Nonnull Currency type) {
        return this.currencies.getOrDefault(Objects.requireNonNull(type, "type cannot be null!"), 0);
    }

    /**
     * Adds value to currency.
     * NOTE: Currency value cannot be negative.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull Currency type, int value) {
        this.currencies.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from currency.
     * NOTE: Currency value cannot be negative.
     *
     * @param type  Player currency type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull Currency type, int value) {
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
     * Converts player currencies object to document. (MONGO BSON)
     *
     * @return Player currencies document.
     */
    @Nonnull
    public Document toDocument() {
        //Creates empty document.
        Document document = new Document();

        //Sets base fields.
        this.currencies.forEach((currency, value) -> document.put(currency.name(), value));

        //Returns created document.
        return document;
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
        json_object.keySet().forEach(currency_string -> this.currencies.put(Currency.valueOf(currency_string), json_object.get(currency_string).getAsInt()));
    }
}

package com.barden.bravo.player;

import com.barden.bravo.player.currency.PlayerCurrencies;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.bravo.player.stats.PlayerStats;
import com.barden.library.cache.MetadataCachedEntity;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Player class.
 */
public final class Player extends MetadataCachedEntity {

    private final long id;
    private final PlayerInventory inventory;
    private final PlayerStats stats;
    private final PlayerCurrencies currencies;

    /**
     * Creates player object.
     *
     * @param id Roblox user id.
     */
    public Player(long id) {
        super(5, TimeUnit.MINUTES, action -> PlayerRepository.remove(id));
        this.id = id;
        this.inventory = new PlayerInventory(this);
        this.stats = new PlayerStats(this);
        this.currencies = new PlayerCurrencies(this);
    }

    /**
     * Creates player object.
     *
     * @param id           Roblox user id.
     * @param bsonDocument Bson document. (FROM MONGO)
     */
    public Player(long id, @Nonnull BsonDocument bsonDocument) {
        super(5, TimeUnit.MINUTES, action -> PlayerRepository.remove(id));
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "player bson document cannot be null!");

        this.id = id;
        this.inventory = new PlayerInventory(this, bsonDocument.getDocument("inventory"));
        this.stats = new PlayerStats(this, bsonDocument.getDocument("stats"));
        this.currencies = new PlayerCurrencies(this, bsonDocument.getDocument("currencies"));
    }

    /**
     * Gets id.
     *
     * @return Roblox user id.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets inventory.
     *
     * @return Player inventory.
     */
    @Nonnull
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets stats.
     *
     * @return Player stats.
     */
    @Nonnull
    public PlayerStats getStats() {
        return this.stats;
    }

    /**
     * Gets currencies.
     *
     * @return Player currencies.
     */
    @Nonnull
    public PlayerCurrencies getCurrencies() {
        return this.currencies;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player as a json object.
     *
     * @return Player as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures fields.
        json_object.addProperty("id", this.id);
        json_object.add("inventory", this.inventory.toJsonObject());
        json_object.add("stats", this.stats.toJsonObject());
        json_object.add("currencies", this.currencies.toJsonObject());

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player object to bson document.
     *
     * @return Player bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();

        //Sets base fields.
        bson_document.put("id", new BsonInt64(this.id));
        bson_document.put("inventory", this.inventory.toBsonDocument());
        bson_document.put("stats", this.stats.toBsonDocument());
        bson_document.put("currencies", this.currencies.toBsonDocument());

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player json object cannot be null!");

        //Handles updates.
        this.inventory.update(json_object.getAsJsonObject("inventory"));
        this.stats.update(json_object.getAsJsonObject("stats"));
        this.currencies.update(json_object.getAsJsonObject("currencies"));

        //Resets cache time.
        this.resetCacheTime();
    }


    /*
    DATABASE
     */

    /**
     * Gets player object's query field.
     *
     * @return Bson. (MONGO)
     */
    @Nonnull
    public Bson toQueryBson() {
        return new BsonDocument("id", new BsonInt64(this.id));
    }

    /**
     * Gets player object as update bson.
     * With update bson, we can update mongo player document.
     *
     * @return Bson. (MONGO)
     */
    @Nonnull
    public Bson toUpdateBson() {
        return Updates.combine(
                Updates.set("inventory", this.inventory.toBsonDocument()),
                Updates.set("stats", this.stats.toBsonDocument()),
                Updates.set("currencies", this.currencies.toBsonDocument()));
    }
}

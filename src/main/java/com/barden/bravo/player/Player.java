package com.barden.bravo.player;

import com.barden.bravo.player.currencies.PlayerCurrencies;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.bravo.player.settings.PlayerSettings;
import com.barden.bravo.player.statistics.PlayerStatistics;
import com.barden.bravo.player.stats.PlayerStats;
import com.barden.library.cache.MetadataCachedEntity;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Player class.
 */
public final class Player extends MetadataCachedEntity {

    private final long id;
    private String name;
    private final PlayerInventory inventory;
    private final PlayerCurrencies currencies;
    private final PlayerStats stats;
    private final PlayerSettings settings;
    private final PlayerStatistics statistics;

    /**
     * Creates player object.
     *
     * @param id Roblox user id.
     */
    public Player(long id, @Nonnull String name) {
        super(15, TimeUnit.MINUTES, action -> PlayerRepository.remove(id));
        this.id = id;
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.inventory = new PlayerInventory(this);
        this.currencies = new PlayerCurrencies(this);
        this.stats = new PlayerStats(this);
        this.settings = new PlayerSettings(this);
        this.statistics = new PlayerStatistics(this);
    }

    /**
     * Creates player object.
     *
     * @param id           Roblox user id.
     * @param bsonDocument Bson document. (FROM MONGO)
     */
    public Player(long id, @Nonnull BsonDocument bsonDocument) {
        super(15, TimeUnit.MINUTES, action -> PlayerRepository.remove(id));
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "player bson document cannot be null!");

        this.id = id;
        this.name = Objects.requireNonNull(bsonDocument.getString("name"), "name cannot be null!").getValue();
        this.inventory = new PlayerInventory(this, bsonDocument.getDocument("inventory"));
        this.currencies = new PlayerCurrencies(this, bsonDocument.getDocument("currencies"));
        this.stats = new PlayerStats(this, bsonDocument.getDocument("stats"));
        this.settings = new PlayerSettings(this, bsonDocument.getDocument("settings"));
        this.statistics = new PlayerStatistics(this, bsonDocument.getDocument("statistics"));
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
     * Gets roblox name.
     *
     * @return Roblox name.
     */
    @Nonnull
    public String getName() {
        return this.name;
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
     * Gets currencies.
     *
     * @return Player currencies.
     */
    @Nonnull
    public PlayerCurrencies getCurrencies() {
        return this.currencies;
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
     * Gets settings.
     *
     * @return Player settings.
     */
    @Nonnull
    public PlayerSettings getSettings() {
        return this.settings;
    }

    /**
     * Gets player statistics.
     *
     * @return Player statistics.
     */
    @Nonnull
    public PlayerStatistics getStatistics() {
        return this.statistics;
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
        json_object.addProperty("name", this.name);
        json_object.add("inventory", this.inventory.toJsonObject());
        json_object.add("currencies", this.currencies.toJsonObject());
        json_object.add("stats", this.stats.toJsonObject());
        json_object.add("settings", this.settings.toJsonObject());
        json_object.add("statistics", this.statistics.toJsonObject());

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
        bson_document.put("name", new BsonString(this.name));
        bson_document.put("inventory", this.inventory.toBsonDocument());
        bson_document.put("currencies", this.currencies.toBsonDocument());
        bson_document.put("stats", this.stats.toBsonDocument());
        bson_document.put("settings", this.settings.toBsonDocument());
        bson_document.put("statistics", this.statistics.toBsonDocument());

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
        this.name = json_object.get("name").getAsString();
        this.inventory.update(json_object.getAsJsonObject("inventory"));
        this.currencies.update(json_object.getAsJsonObject("currencies"));
        this.stats.update(json_object.getAsJsonObject("stats"));
        //this.settings.update(json_object.getAsJsonObject("settings"));
        this.statistics.update(json_object.getAsJsonObject("statistics"));

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
                Updates.set("name", this.name),
                Updates.set("inventory", this.inventory.toBsonDocument()),
                Updates.set("currencies", this.currencies.toBsonDocument()),
                Updates.set("stats", this.stats.toBsonDocument()),
                Updates.set("settings", this.settings.toBsonDocument()),
                Updates.set("statistics", this.statistics.toBsonDocument()));
    }
}

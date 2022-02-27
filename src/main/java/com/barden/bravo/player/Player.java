package com.barden.bravo.player;

import com.barden.bravo.player.currencies.PlayerCurrencies;
import com.barden.bravo.player.database.PlayerDatabase;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.bravo.player.settings.PlayerSettings;
import com.barden.bravo.player.statistics.PlayerStatistics;
import com.barden.bravo.player.stats.PlayerStats;
import com.barden.library.cache.MetadataCachedEntity;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

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
    private final PlayerDatabase database;

    /**
     * Creates a player.
     *
     * @param id Roblox user id.
     */
    public Player(long id, @Nonnull String name) {
        super(15, TimeUnit.MINUTES, action -> PlayerProvider.remove(id));
        this.id = id;
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.inventory = new PlayerInventory(this);
        this.currencies = new PlayerCurrencies(this);
        this.stats = new PlayerStats(this);
        this.settings = new PlayerSettings(this);
        this.statistics = new PlayerStatistics(this);
        this.database = new PlayerDatabase(this);
    }

    /**
     * Creates a player from a bson document.
     *
     * @param id       Roblox user id.
     * @param document Player bson document.
     */
    public Player(long id, @Nonnull BsonDocument document) {
        super(15, TimeUnit.MINUTES, action -> PlayerProvider.remove(id));
        //Objects null check.
        Objects.requireNonNull(document, "player bson document cannot be null!");

        this.id = id;
        this.name = Objects.requireNonNull(document.getString("name"), "name cannot be null!").getValue();
        this.inventory = new PlayerInventory(this, document.getDocument("inventory"));
        this.currencies = new PlayerCurrencies(this, document.getDocument("currencies"));
        this.stats = new PlayerStats(this, document.getDocument("stats"));
        this.settings = new PlayerSettings(this, document.getDocument("settings"));
        this.statistics = new PlayerStatistics(this, document.getDocument("statistics"));
        this.database = new PlayerDatabase(this);
    }

    /**
     * Gets player roblox user id.
     *
     * @return Player roblox user id.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets player roblox name.
     *
     * @return Player roblox name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Gets player inventory.
     *
     * @return Player inventory.
     */
    @Nonnull
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets player currencies.
     *
     * @return Player currencies.
     */
    @Nonnull
    public PlayerCurrencies getCurrencies() {
        return this.currencies;
    }

    /**
     * Gets player stats.
     *
     * @return Player stats.
     */
    @Nonnull
    public PlayerStats getStats() {
        return this.stats;
    }

    /**
     * Gets player settings.
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

    /**
     * Gets player database.
     *
     * @return Player database.
     */
    @Nonnull
    public PlayerDatabase getDatabase() {
        return this.database;
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player to a json object.
     *
     * @return Player json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.addProperty("name", this.name);
        json.add("inventory", this.inventory.toJsonObject());
        json.add("currencies", this.currencies.toJsonObject());
        json.add("stats", this.stats.toJsonObject());
        json.add("settings", this.settings.toJsonObject());
        json.add("statistics", this.statistics.toJsonObject());
        return json;
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

        this.name = json_object.get("name").getAsString();
        this.inventory.update(json_object.getAsJsonObject("inventory"));
        this.currencies.update(json_object.getAsJsonObject("currencies"));
        this.stats.update(json_object.getAsJsonObject("stats"));
        //this.settings.update(json_object.getAsJsonObject("settings"));
        this.statistics.update(json_object.getAsJsonObject("statistics"));

        //Resets cache time.
        this.resetCacheTime();
    }
}

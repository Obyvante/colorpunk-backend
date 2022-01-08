package com.barden.bravo.player;

import com.barden.bravo.player.currency.PlayerCurrencies;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.bravo.player.stats.PlayerStats;
import com.barden.library.database.DatabaseRepository;
import com.barden.library.metadata.MetadataEntity;
import com.barden.library.scheduler.SchedulerRepository;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Player class.
 */
public final class Player extends MetadataEntity {

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
        this.id = id;
        this.inventory = new PlayerInventory(this);
        this.stats = new PlayerStats(this);
        this.currencies = new PlayerCurrencies(this);
    }

    /**
     * Creates player object.
     *
     * @param id Roblox user id.
     */
    public Player(long id, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "document cannot be null!");

        this.id = id;
        this.inventory = new PlayerInventory(this, document.get("inventory", Document.class));
        this.stats = new PlayerStats(this);
        //this.stats = new PlayerStats(this, document.get("stats", Document.class));
        this.currencies = new PlayerCurrencies(this, document.get("currencies", Document.class));
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
        json_object.add("currencies", this.currencies.toJsonObject());

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player object to document. (MONGO BSON)
     *
     * @return Player document.
     */
    @Nonnull
    public Document toDocument() {
        //Creates empty document.
        Document document = new Document();

        //Sets base fields.
        document.put("id", this.id);
        document.put("inventory", this.inventory.toDocument());
        document.put("currencies", this.currencies.toDocument());

        //Returns created document.
        return document;
    }


    /*
    DATABASE
     */

    /**
     * Saves player to the database.
     */
    public void save() {
        //If player is already exist in repository, no need to continue.
        if (PlayerRepository.find(this.id).isPresent())
            return;

        //Handles database saving in a task.
        SchedulerRepository.schedule(task -> {
            //Gets mongo collection.
            MongoCollection<Document> collection = DatabaseRepository.mongo().getCollection("bravo", "players");
            //If collection is null, no need to continue.
            if (collection == null)
                return;

            //Declare base variables
            Document user_id = new Document("id", this.id);

            //If player is already created, no need to continue.
            if (collection.find(user_id).limit(1).cursor().hasNext())
                return;

            //Saves/updates to the database.
            collection.updateOne(user_id, new Document("$set", this.toDocument()), new UpdateOptions().upsert(true));
        });
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
        this.currencies.update(json_object.getAsJsonObject("currencies"));
        this.inventory.update(json_object.getAsJsonObject("inventory"));
    }
}

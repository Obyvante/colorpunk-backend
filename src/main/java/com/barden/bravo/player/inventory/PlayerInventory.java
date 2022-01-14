package com.barden.bravo.player.inventory;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.pet.PlayerPetInventory;
import com.barden.bravo.player.inventory.trail.PlayerTrailInventory;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Player inventory class.
 */
public final class PlayerInventory extends MetadataEntity {

    private final Player player;
    private final PlayerPetInventory petInventory;
    private final PlayerTrailInventory trailInventory;

    /**
     * Creates player inventory object.
     *
     * @param player Player.
     */
    public PlayerInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.petInventory = new PlayerPetInventory(this.player, this);
        this.trailInventory = new PlayerTrailInventory(this.player, this);
    }

    /**
     * Creates player inventory object.
     *
     * @param player       Player.
     * @param bsonDocument Mongo bson document. (INVENTORY BSON)
     */
    public PlayerInventory(@Nonnull Player player, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "player inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.petInventory = new PlayerPetInventory(this.player, this, Objects.requireNonNull(bsonDocument.getDocument("pets"), "pets bson document cannot be null!"));
        this.trailInventory = new PlayerTrailInventory(this.player, this, Objects.requireNonNull(bsonDocument.getDocument("trails"), "trails bson document cannot be null!"));
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
     * Gets pet inventory.
     *
     * @return Player pet inventory.
     */
    @Nonnull
    public PlayerPetInventory getPet() {
        return this.petInventory;
    }

    /**
     * Gets trail inventory.
     *
     * @return Player trial inventory.
     */
    @Nonnull
    public PlayerTrailInventory getTrail() {
        return this.trailInventory;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player inventory as a json object.
     *
     * @return Player inventory object as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures json properties.
        json_object.add("pets", this.petInventory.toJsonObject());
        json_object.add("trails", this.trailInventory.toJsonObject());

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player inventory object to bson document.
     *
     * @return Player inventory bson document. (MONGO)
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty document.
        BsonDocument document = new BsonDocument();

        //Configures document.
        document.put("pets", this.petInventory.toBsonDocument());
        document.put("trails", this.trailInventory.toBsonDocument());

        //Returns created document.
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player inventory object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player inventory json object cannot be null!");

        //Handles inventory updates.
        this.petInventory.update(json_object.getAsJsonObject("pets"));
        this.trailInventory.update(json_object.getAsJsonObject("trails"));
    }
}

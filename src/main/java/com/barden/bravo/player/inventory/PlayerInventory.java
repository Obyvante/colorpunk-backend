package com.barden.bravo.player.inventory;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.pet.PlayerPetInventory;
import com.barden.bravo.player.inventory.trail.PlayerTrailInventory;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonObject;
import org.bson.Document;

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
     * @param player   Player.
     * @param document Mongo document. (INVENTORY BSON)
     */
    public PlayerInventory(@Nonnull Player player, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "player inventory document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.petInventory = new PlayerPetInventory(this.player, this, Objects.requireNonNull(document.get("pets", Document.class), "pets document cannot be null!"));

        //TODO: will add database support.
        this.trailInventory = new PlayerTrailInventory(this.player, this);
        //this.trailInventory = new PlayerTrailInventory(this.player, this, Objects.requireNonNull(document.get("trails", Document.class), "trails document cannot be null!"));
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

        //Adds player pet inventory json object to the created json object.
        json_object.add("pets", this.petInventory.toJsonObject());

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player inventory object to document. (MONGO BSON)
     *
     * @return Player inventory document.
     */
    @Nonnull
    public Document toDocument() {
        //Creates empty document.
        Document document = new Document();

        //Adds player pet inventory document object to the created document.
        document.put("pets", this.petInventory.toDocument());

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

        //Updates player pet inventory object.
        this.petInventory.update(json_object.getAsJsonObject("pets"));
    }
}

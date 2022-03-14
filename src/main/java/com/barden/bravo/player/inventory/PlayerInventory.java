package com.barden.bravo.player.inventory;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.pet.PlayerPetInventory;
import com.barden.bravo.player.inventory.product.PlayerProductInventory;
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
    private final PlayerPetInventory pet;
    private final PlayerTrailInventory trail;
    private final PlayerProductInventory product;

    /**
     * Creates a player inventory.
     *
     * @param player Player.
     */
    public PlayerInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.pet = new PlayerPetInventory(this.player);
        this.trail = new PlayerTrailInventory(this.player);
        this.product = new PlayerProductInventory(this.player);
    }

    /**
     * Creates a player inventory from a bson document.
     *
     * @param player   Player.
     * @param document Player inventory bson document.
     */
    public PlayerInventory(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.pet = new PlayerPetInventory(this.player, Objects.requireNonNull(document.getDocument("pets"), "pets bson document cannot be null!"));
        this.trail = new PlayerTrailInventory(this.player, Objects.requireNonNull(document.getDocument("trails"), "trails bson document cannot be null!"));
        this.product = new PlayerProductInventory(this.player, Objects.requireNonNull(document.getDocument("products"), "products bson document cannot be null!"));
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
     * Gets player pet inventory.
     *
     * @return Player pet inventory.
     */
    @Nonnull
    public PlayerPetInventory getPet() {
        return this.pet;
    }

    /**
     * Gets player trail inventory.
     *
     * @return Player trial inventory.
     */
    @Nonnull
    public PlayerTrailInventory getTrail() {
        return this.trail;
    }

    /**
     * Gets player product inventory.
     *
     * @return Player product inventory.
     */
    @Nonnull
    public PlayerProductInventory getProduct() {
        return this.product;
    }

    /*
    CONVERTERS
     */

    /**
     * Converts player inventory to a json object.
     *
     * @return Player inventory json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.add("pets", this.pet.toJsonObject());
        json.add("trails", this.trail.toJsonObject());
        json.add("products", this.product.toJsonObject());
        return json;
    }

    /**
     * Converts player inventory to a bson document.
     *
     * @return Player inventory bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        document.put("pets", this.pet.toBsonDocument());
        document.put("trails", this.trail.toBsonDocument());
        document.put("products", this.product.toBsonDocument());
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player inventory.
     *
     * @param json Player inventory json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player inventory json object cannot be null!");

        this.pet.update(json.getAsJsonObject("pets"));
        this.trail.update(json.getAsJsonObject("trails"));
        this.product.update(json.getAsJsonObject("products"));
    }
}

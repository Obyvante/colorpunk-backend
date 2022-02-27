package com.barden.bravo.player.cosmetics.pet;

import com.barden.bravo.cosmetics.pet.Pet;
import com.barden.bravo.cosmetics.pet.PetProvider;
import com.barden.bravo.player.Player;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonObject;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * Player pet class.
 */
public final class PlayerPet extends MetadataEntity {

    private final Player player;
    private final UUID uid;
    private final int id;
    private boolean active;

    /**
     * Creates a player pet.
     *
     * @param player Player.
     * @param uid    Player pet unique id.
     * @param id     Pet id.
     * @param active Player pet active status.
     */
    public PlayerPet(@Nonnull Player player, @Nonnull UUID uid, int id, boolean active) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.uid = Objects.requireNonNull(uid, "player pet uid cannot be null!");
        this.id = id;
        this.active = active;
    }

    /**
     * Creates a player pet from a json object.
     *
     * @param player Player.
     * @param uid    Player Pet unique id.
     * @param json   Player pet json object.
     */
    public PlayerPet(@Nonnull Player player, @Nonnull UUID uid, @Nonnull JsonObject json) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.uid = Objects.requireNonNull(uid, "player pet uid cannot be null!");
        this.id = json.get("id").getAsInt();
        this.active = json.get("active").getAsBoolean();
    }

    /**
     * Gets pet.
     *
     * @return Pet.
     */
    @Nonnull
    public Pet getPet() {
        return PetProvider.get(this.id);
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
     * Gets player pet unique id.
     *
     * @return Player pet unique id.
     */
    @Nonnull
    public UUID getUID() {
        return this.uid;
    }

    /**
     * Gets pet id.
     *
     * @return Pet id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets if player pet is active or not.
     *
     * @return If player pet is active or not.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets player pet status.
     *
     * @param status Player pet status. (TRUE = active, FALSE = inactive)
     */
    public void setActive(boolean status) {
        if (this.active == status)
            return;
        this.active = status;
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player pet to a json object.
     *
     * @return Player pet json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.addProperty("active", this.active);
        return json;
    }

    /**
     * Converts player pet to a bson document.
     *
     * @return Player pet bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        document.put("id", new BsonInt32(this.id));
        document.put("active", new BsonBoolean(this.active));
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player pet.
     *
     * @param json Player pet json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player pet json object cannot be null!");

        this.setActive(json.get("active").getAsBoolean());
    }
}

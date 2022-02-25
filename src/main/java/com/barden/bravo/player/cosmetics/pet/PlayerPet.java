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
     * Creates player pet object.
     *
     * @param player Player.
     * @param uid    Player pet unique id.
     * @param id     Pet id.
     * @param active Player pet active status.
     */
    public PlayerPet(@Nonnull Player player, @Nonnull UUID uid, int id, boolean active) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.uid = Objects.requireNonNull(uid, "uid cannot be null!");
        this.id = id;
        this.active = active;
    }

    /**
     * Creates player pet object from json object.
     *
     * @param player      Player.
     * @param uid         Player Pet unique id.
     * @param json_object Player pet json object.
     */
    public PlayerPet(@Nonnull Player player, @Nonnull UUID uid, @Nonnull JsonObject json_object) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.uid = Objects.requireNonNull(uid, "uid cannot be null!");
        this.id = json_object.get("id").getAsInt();
        this.active = json_object.get("active").getAsBoolean();
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
     * Gets id.
     *
     * @return Pet id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets UID.
     *
     * @return Pet unique id.
     */
    @Nonnull
    public UUID getUID() {
        return this.uid;
    }

    /**
     * Gets if pet is active or not.
     *
     * @return If player pet is active or not.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets pet status.
     *
     * @param status Player pet status. (TRUE = active, FALSE = inactive)
     */
    public void setActive(boolean status) {
        //If player pet is already in same status, no need to continue.
        if (this.active == status)
            return;
        //Changes player pet status.
        this.active = status;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player pet as a json object.
     *
     * @return Player pet as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures class fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("active", this.active);

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player pet object to bson document.
     *
     * @return Player pet bson document. (BSON)
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();

        //Sets base fields.
        bson_document.put("id", new BsonInt32(this.id));
        bson_document.put("active", new BsonBoolean(this.active));

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player pet object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player pet json object cannot be null!");

        //Updates player pet status.
        this.setActive(json_object.get("active").getAsBoolean());
    }
}

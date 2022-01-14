package com.barden.bravo.player.inventory.trail;

import com.barden.bravo.cosmetics.trail.Trail;
import com.barden.bravo.cosmetics.trail.TrailRepository;
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
 * Player trail class.
 */
public final class PlayerTrail extends MetadataEntity {

    private final Player player;
    private final PlayerTrailInventory inventory;

    private final int id;
    private final UUID uid;
    private boolean active;

    /**
     * Creates player pet object.
     *
     * @param player    Player.
     * @param inventory Player trail inventory.
     * @param id        Pet id.
     * @param uid       Player trail unique id.
     * @param active    Player trail active status.
     */
    public PlayerTrail(@Nonnull Player player, @Nonnull PlayerTrailInventory inventory, int id, @Nonnull UUID uid, boolean active) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player pet inventory cannot be null!");
        this.id = id;
        this.uid = Objects.requireNonNull(uid, "uid cannot be null!");
        this.active = active;
    }

    /**
     * Creates player trail object from json object.
     *
     * @param player      Player.
     * @param inventory   Player trail inventory.
     * @param uid         Player trail unique id.
     * @param json_object Player trail json object.
     */
    public PlayerTrail(@Nonnull Player player, @Nonnull PlayerTrailInventory inventory, @Nonnull UUID uid, @Nonnull JsonObject json_object) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player trail inventory cannot be null!");
        this.uid = Objects.requireNonNull(uid, "uid cannot be null!");
        this.id = json_object.get("id").getAsInt();
        this.active = json_object.get("active").getAsBoolean();
    }

    /**
     * Gets trail.
     *
     * @return Trail.
     */
    @Nonnull
    public Trail getTrail() {
        return TrailRepository.get(this.id);
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
     * Gets trail inventory.
     *
     * @return Player trail inventory.
     */
    @Nonnull
    public PlayerTrailInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets id.
     *
     * @return Trail id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets UID.
     *
     * @return Trail unique id.
     */
    @Nonnull
    public UUID getUID() {
        return this.uid;
    }

    /**
     * Gets if trail is active or not.
     *
     * @return If player trail is active or not.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets trail status.
     *
     * @param status Player trail status. (TRUE = active, FALSE = inactive)
     */
    public void setActive(boolean status) {
        //If player trail is already in same status, no need to continue.
        if (this.active == status)
            return;

        //Changes player trail status.
        this.active = status;
        //Updates active status of player trail.
        this.inventory.updateActive(this);
    }

    /**
     * Checks if player trail is exist or not.
     *
     * @return If player trail is exist or not.
     */
    public boolean isExist() {
        return this.inventory.find(this.uid).isPresent();
    }

    /**
     * Deletes player trail.
     */
    public void delete() {
        this.inventory.remove(this);
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player trail as a json object.
     *
     * @return Player trail as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("active", this.active);

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player trail object to bson document.
     *
     * @return Player trail bson document. (BSON)
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
     * Updates player trail object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player trail json object cannot be null!");

        //Updates player trail status.
        this.setActive(json_object.get("active").getAsBoolean());
    }
}

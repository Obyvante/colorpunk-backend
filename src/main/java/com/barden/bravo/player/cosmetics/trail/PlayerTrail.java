package com.barden.bravo.player.cosmetics.trail;

import com.barden.bravo.cosmetics.trail.Trail;
import com.barden.bravo.cosmetics.trail.TrailProvider;
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
    private final UUID uid;
    private final int id;
    private boolean active;

    /**
     * Creates a player pet.
     *
     * @param player Player.
     * @param uid    Player trail unique id
     * @param id     Trail id.
     * @param active Player trail active status.
     */
    public PlayerTrail(@Nonnull Player player, @Nonnull UUID uid, int id, boolean active) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.uid = Objects.requireNonNull(uid, "player trail uid cannot be null!");
        this.id = id;
        this.active = active;
    }

    /**
     * Creates a player trail from a json object.
     *
     * @param player Player.
     * @param uid    Player trail unique id.
     * @param json   Player trail json object.
     */
    public PlayerTrail(@Nonnull Player player, @Nonnull UUID uid, @Nonnull JsonObject json) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.uid = Objects.requireNonNull(uid, "player trail uid cannot be null!");
        this.id = json.get("id").getAsInt();
        this.active = json.get("active").getAsBoolean();
    }

    /**
     * Gets trail.
     *
     * @return Trail.
     */
    @Nonnull
    public Trail getTrail() {
        return TrailProvider.get(this.id);
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
     * Gets player trail unique id.
     *
     * @return Player trail unique id.
     */
    @Nonnull
    public UUID getUID() {
        return this.uid;
    }

    /**
     * Gets trail id.
     *
     * @return Trail id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets if player trail is active or not.
     *
     * @return If player trail is active or not.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets player trail status.
     *
     * @param status Player trail status. (TRUE = active, FALSE = inactive)
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
     * Converts player trail to a json object.
     *
     * @return Player trail json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.addProperty("active", this.active);
        return json;
    }

    /**
     * Converts player trail to a bson document.
     *
     * @return Player trail bson document.
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
     * Updates player trail.
     *
     * @param json Player trail json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player trail json object cannot be null!");

        this.setActive(json.get("active").getAsBoolean());
    }
}

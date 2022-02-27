package com.barden.bravo.player.inventory.trail;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.cosmetics.trail.PlayerTrail;
import com.barden.library.metadata.MetadataEntity;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Player trail inventory class.
 */
public final class PlayerTrailInventory extends MetadataEntity {

    private final Player player;
    private final BiMap<UUID, PlayerTrail> content = HashBiMap.create();

    /**
     * Creates player trail inventory object.
     *
     * @param player Player.
     */
    public PlayerTrailInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player trail inventory object.
     *
     * @param player   Player.
     * @param document Bson document.
     */
    public PlayerTrailInventory(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player trail inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Loops trail bson documents.
        document.keySet().forEach(trail_uid_string -> {
            //Declares required fields.
            @Nonnull BsonDocument trail_document = Objects.requireNonNull(document.getDocument(trail_uid_string), "player trail bson document cannot be null!");
            @Nonnull UUID trail_uid = UUID.fromString(trail_uid_string);
            int trail_id = Objects.requireNonNull(trail_document.getInt32("id"), "trail id cannot be null!").intValue();
            boolean trail_active = trail_document.getBoolean("active").getValue();

            //Creates new player trail then adds to the trails list.
            this.content.put(trail_uid, new PlayerTrail(this.player, trail_uid, trail_id, trail_active));
        });
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
     * Gets trails.
     *
     * @return Player trails.
     */
    @Nonnull
    public Set<PlayerTrail> getContent() {
        return this.content.values();
    }

    /**
     * Finds player trail. (SAFE)
     *
     * @param uid Player trail unique id.
     * @return Optional player trail.
     */
    @Nonnull
    public Optional<PlayerTrail> find(@Nonnull UUID uid) {
        return Optional.ofNullable(this.content.get(Objects.requireNonNull(uid)));
    }

    /**
     * Gets player trail. (UNSAFE)
     *
     * @param uid Player trail unique id.
     * @return Player trail.
     */
    @Nonnull
    public PlayerTrail get(@Nonnull UUID uid) {
        return this.find(uid).orElseThrow(() -> new NullPointerException("player trail cannot be null!"));
    }

    /**
     * Creates a trail.
     *
     * @param id Trail id.
     * @return Player trail.
     */
    @Nonnull
    public PlayerTrail add(int id) {
        //Creates a player trail.
        PlayerTrail player_trail = new PlayerTrail(this.player, UUID.randomUUID(), id, false);
        //Adds created trail to the trails list.
        this.content.put(player_trail.getUID(), player_trail);
        //Returns created trail.
        return player_trail;
    }

    /**
     * Removes player trail by its unique id from trails list.
     *
     * @param id Player trail unique id.
     */
    public void remove(@Nonnull UUID id) {
        this.content.remove(Objects.requireNonNull(id));
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player trail inventory as a json object.
     *
     * @return Player trail inventory as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        this.content.forEach((key, value) -> object.add(key.toString(), value.toJsonObject()));
        return object;
    }

    /**
     * Converts player trail inventory to bson document.
     *
     * @return Player trail inventory bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        this.content.forEach((key, value) -> document.put(key.toString(), value.toBsonDocument()));
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player trail inventory.
     *
     * @param object Json object.
     */
    public void update(@Nonnull JsonObject object) {
        //Objects null check.
        Objects.requireNonNull(object, "player trail inventory json object cannot be null!");

        //Loops through trails.
        this.content.forEach((key, value) -> {
            //Handles existing trails.
            if (object.keySet().contains(key.toString()))
                value.update(object.getAsJsonObject(key.toString())); //If trail is exist in the content json object.
            else
                this.remove(key);
        });

        //Handles new trails.
        object.keySet().stream().filter(trail_uid_string -> this.find(UUID.fromString(trail_uid_string)).isEmpty()).forEach(trail_uid_string -> {
            //Declares trail uid.
            @Nonnull UUID trail_uid = UUID.fromString(trail_uid_string);
            //Creates new player trail object then adds to the content list.
            this.content.put(trail_uid, new PlayerTrail(this.player, trail_uid, object.getAsJsonObject(trail_uid_string)));
        });
    }
}

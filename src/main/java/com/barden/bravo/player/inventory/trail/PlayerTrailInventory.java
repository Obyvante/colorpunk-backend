package com.barden.bravo.player.inventory.trail;

import com.barden.bravo.cosmetics.trail.TrailProvider;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.cosmetics.trail.PlayerTrail;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.library.metadata.MetadataEntity;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Player trail inventory class.
 */
public final class PlayerTrailInventory extends MetadataEntity {

    private final Player player;
    private final BiMap<UUID, PlayerTrail> content = HashBiMap.create();

    /**
     * Creates a player trail inventory.
     *
     * @param player Player.
     */
    public PlayerTrailInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates a player trail inventory from a bson document.
     *
     * @param player   Player.
     * @param document Player trail inventory bson document.
     */
    public PlayerTrailInventory(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player trail inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

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
     * Gets player trails.
     *
     * @return Player trails.
     */
    @Nonnull
    public HashSet<PlayerTrail> getContent() {
        return Sets.newHashSet(this.content.values());
    }

    /**
     * Gets player trails by its state.
     *
     * @param state Player trail state.
     * @return Player trails.
     */
    @Nonnull
    public HashSet<PlayerTrail> getContentByState(boolean state) {
        HashSet<PlayerTrail> _content = new HashSet<>();
        this.content.values().forEach(value -> {
            if (value.isActive() == state)
                _content.add(value);
        });
        return _content;
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
     * Creates a player trail.
     *
     * @param id Trail id.
     * @return Player trail.
     */
    @Nonnull
    public PlayerTrail add(int id) {
        if (TrailProvider.find(id).isEmpty())
            throw new NullPointerException("trail(" + id + ") does not exist!");

        if (this.content.size() >= PlayerInventory.INVENTORY_SIZE)
            throw new IllegalStateException("player(" + this.player.getId() + ") inventory size must be equals or lower than " + PlayerInventory.INVENTORY_SIZE);

        PlayerTrail trail = new PlayerTrail(this.player, UUID.randomUUID(), id, false);
        this.content.put(trail.getUID(), trail);

        return trail;
    }

    /**
     * Removes player trail.
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
     * Converts player trail inventory to a json object.
     *
     * @return Player trail inventory json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        this.content.forEach((key, value) -> object.add(key.toString(), value.toJsonObject()));
        return object;
    }

    /**
     * Converts player trail inventory to a bson document.
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
     * @param json Player trail json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player trail inventory json object cannot be null!");

        //Declares required fields.
        HashMap<UUID, JsonObject> _content = new HashMap<>();
        json.entrySet().forEach(entry -> _content.put(UUID.fromString(entry.getKey()), entry.getValue().getAsJsonObject()));

        //Removing and updating existing ones.
        this.getContent().forEach(_item -> {
            var _uid = _item.getUID();

            //If item is existed, updates it.
            if (_content.containsKey(_uid)) {
                _item.update(_content.get(_uid));
                return;
            }

            //Removes item.
            this.remove(_uid);
        });

        //Handles new player items.
        json.entrySet().forEach(entry -> {
            //Declares required fields.
            var _uid = UUID.fromString(entry.getKey());

            //If item is existed, no need to continue.
            if (this.find(_uid).isPresent())
                return;

            //Adds item to the player's inventory.
            this.content.put(_uid, new PlayerTrail(this.player, _uid, entry.getValue().getAsJsonObject()));
        });
    }
}

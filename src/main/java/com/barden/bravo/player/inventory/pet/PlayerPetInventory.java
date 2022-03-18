package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.cosmetics.pet.PetProvider;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.cosmetics.pet.PlayerPet;
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
 * Player pet inventory class.
 */
public final class PlayerPetInventory extends MetadataEntity {

    private final Player player;
    private final BiMap<UUID, PlayerPet> content = HashBiMap.create();

    /**
     * Creates player pet inventory.
     *
     * @param player Player.
     */
    public PlayerPetInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player pet inventory from a bson document.
     *
     * @param player   Player.
     * @param document Player pet inventory bson document.
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player pet inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        document.keySet().forEach(pet_uid_string -> {
            //Declares required fields.
            var pet_document = Objects.requireNonNull(document.getDocument(pet_uid_string), "player pet bson document cannot be null!");
            var pet_id = Objects.requireNonNull(pet_document.getInt32("id"), "pet id cannot be null!").intValue();
            var pet_active = pet_document.getBoolean("active").getValue();
            var pet_uid = UUID.fromString(pet_uid_string);

            //Creates new player pet then adds to the pets list.
            this.content.put(pet_uid, new PlayerPet(this.player, pet_uid, pet_id, pet_active));
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
     * Gets player pets.
     *
     * @return Player pets.
     */
    @Nonnull
    public HashSet<PlayerPet> getContent() {
        return Sets.newHashSet(this.content.values());
    }

    /**
     * Gets player pets by its state.
     *
     * @param state Player pet state.
     * @return Player pets.
     */
    @Nonnull
    public HashSet<PlayerPet> getContentByState(boolean state) {
        HashSet<PlayerPet> _content = new HashSet<>();
        this.content.values().forEach(value -> {
            if (value.isActive() == state)
                _content.add(value);
        });
        return _content;
    }

    /**
     * Finds player pet. (SAFE)
     *
     * @param uid Player pet unique id.
     * @return Optional player pet.
     */
    @Nonnull
    public Optional<PlayerPet> find(@Nonnull UUID uid) {
        return Optional.ofNullable(this.content.get(Objects.requireNonNull(uid)));
    }

    /**
     * Gets player pet. (UNSAFE)
     *
     * @param uid Player pet unique id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet get(@Nonnull UUID uid) {
        return this.find(uid).orElseThrow(() -> new NullPointerException("player pet cannot be null!"));
    }

    /**
     * Creates a player pet.
     *
     * @param id Pet id.
     * @return Created player pet.
     */
    @Nonnull
    public PlayerPet add(int id) {
        if (PetProvider.find(id).isEmpty())
            throw new NullPointerException("pet(" + id + ") does not exist!");

        if (this.content.size() >= PlayerInventory.INVENTORY_SIZE)
            throw new IllegalStateException("player(" + this.player.getId() + ") inventory size must be equals or lower than " + PlayerInventory.INVENTORY_SIZE);

        PlayerPet pet = new PlayerPet(this.player, UUID.randomUUID(), id, false);
        this.content.put(pet.getUID(), pet);

        return pet;
    }

    /**
     * Removes player pet.
     *
     * @param uid Player pet unique id.
     */
    public void remove(@Nonnull UUID uid) {
        this.content.remove(Objects.requireNonNull(uid));
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player pet inventory to a json object.
     *
     * @return Player pet inventory json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        this.content.forEach((key, value) -> json.add(key.toString(), value.toJsonObject()));
        return json;
    }

    /**
     * Converts player pet inventory to a bson document.
     *
     * @return Player pet inventory bson document.
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
     * Updates player pet inventory.
     *
     * @param json Player pet inventory json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player pet inventory json object cannot be null!");

        //Declares required fields.
        HashMap<UUID, JsonObject> _content = new HashMap<>();
        json.entrySet().forEach(entry -> _content.put(UUID.fromString(entry.getKey()), entry.getValue().getAsJsonObject()));

        //Removing and updating existing ones.
        this.getContent().forEach(_pet -> {
            var _uid = _pet.getUID();

            //If pet is exist, updates it.
            if (_content.containsKey(_uid)) {
                _pet.update(_content.get(_uid));
                return;
            }

            //Removes player pet.
            this.remove(_uid);
        });

        //Handles new player pets.
        json.entrySet().forEach(entry -> {
            //Declares required fields.
            var _uid = UUID.fromString(entry.getKey());

            //If pet is exist, no need to continue.
            if (this.find(_uid).isPresent())
                return;

            //Adds pet to the player's pet inventory.
            this.content.put(_uid, new PlayerPet(this.player, _uid, entry.getValue().getAsJsonObject()));
        });
    }
}

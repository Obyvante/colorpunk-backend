package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.cosmetics.pet.PetProvider;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.cosmetics.pet.PlayerPet;
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
            @Nonnull BsonDocument pet_document = Objects.requireNonNull(document.getDocument(pet_uid_string), "player pet bson document cannot be null!");
            int pet_id = Objects.requireNonNull(pet_document.getInt32("id"), "pet id cannot be null!").intValue();
            boolean pet_active = pet_document.getBoolean("active").getValue();
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);

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
    public Set<PlayerPet> getContent() {
        return this.content.values();
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

        PlayerPet pet = new PlayerPet(this.player, UUID.randomUUID(), id, false);
        this.content.put(pet.getUID(), pet);

        return pet;
    }

    /**
     * Removes player pet.
     *
     * @param id Player pet unique id.
     */
    public void remove(@Nonnull UUID id) {
        this.content.remove(Objects.requireNonNull(id));
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

        //Handles existed player pets.
        this.content.forEach((key, value) -> {
            if (json.keySet().contains(key.toString()))
                value.update(json.getAsJsonObject(key.toString()));
            else
                this.remove(key);
        });

        //Handles new player pets.
        json.keySet().stream().filter(pet_uid_string -> this.find(UUID.fromString(pet_uid_string)).isEmpty()).forEach(pet_uid_string -> {
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);
            this.content.put(pet_uid, new PlayerPet(this.player, pet_uid, json.getAsJsonObject(pet_uid_string)));
        });
    }
}

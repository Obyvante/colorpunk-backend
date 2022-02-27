package com.barden.bravo.player.inventory.pet;

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
     * Creates player pet inventory object.
     *
     * @param player Player.
     */
    public PlayerPetInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player pet inventory object.
     *
     * @param player   Player.
     * @param document Bson document. (MONGO)
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "player pet inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Loops pet bson documents.
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
     * Creates a pet.
     *
     * @param id Pet id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet add(int id) {
        //Creates new pet.
        PlayerPet pet = new PlayerPet(this.player, UUID.randomUUID(), id, false);
        //Adds created pet to the pets list.
        this.content.put(pet.getUID(), pet);
        //Returns created pet.
        return pet;
    }

    /**
     * Removes pet from pets list.
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
     * Gets player pet inventory as a json object.
     *
     * @return Player pet inventory as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject object = new JsonObject();
        this.content.forEach((key, value) -> object.add(key.toString(), value.toJsonObject()));
        return object;
    }

    /**
     * Converts player pet inventory to bson document.
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
     * @param object Json object.
     */
    public void update(@Nonnull JsonObject object) {
        //Objects null check.
        Objects.requireNonNull(object, "player pet inventory json object cannot be null!");

        //Loops through pets.
        this.content.forEach((key, value) -> {
            //Handles existing pets.
            if (object.keySet().contains(key.toString()))
                value.update(object.getAsJsonObject(key.toString())); //If pet is exist in the content json object.
            else
                this.remove(key); //Deletes player pet since it is no longer in the inventory.
        });

        //Handles new pets.
        object.keySet().stream().filter(pet_uid_string -> this.find(UUID.fromString(pet_uid_string)).isEmpty()).forEach(pet_uid_string -> {
            //Declares pet uid.
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);
            //Creates new player pet object then adds to the content list.
            this.content.put(pet_uid, new PlayerPet(this.player, pet_uid, object.getAsJsonObject(pet_uid_string)));
        });
    }
}

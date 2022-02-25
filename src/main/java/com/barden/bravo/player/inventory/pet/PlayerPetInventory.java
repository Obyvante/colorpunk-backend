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

        //Gets pet content.
        @Nonnull BsonDocument pets_document = Objects.requireNonNull(document.getDocument("content"), "player pet inventory content bson document cannot be null!");
        //Loops pet bson documents.
        pets_document.keySet().forEach(pet_uid_string -> {
            //Declares required fields.
            @Nonnull BsonDocument pet_document = Objects.requireNonNull(pets_document.getDocument(pet_uid_string), "player pet bson document cannot be null!");
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
     * Gets pets.
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
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures pets.
        JsonObject object = new JsonObject();
        //Loops through pets and adds one by one to the created json object.
        this.content.forEach((key, value) -> object.add(key.toString(), value.toJsonObject()));
        //Adds pet(s) json object to the base json object.
        json_object.add("content", object);

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player pet inventory object to bson document.
     *
     * @return Player pet inventory bson document. (MONGO)
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument document = new BsonDocument();

        //Creates content bson document.
        BsonDocument content_document = new BsonDocument();
        //Loops through content and add pets one by one to the content bson document.
        this.content.forEach((key, value) -> content_document.put(key.toString(), value.toBsonDocument()));
        //Puts content bson document to the base document.
        document.put("content", content_document);

        //Returns created bson document.
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player pet inventory object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player pet inventory json object cannot be null!");

        //Gets content json object from the declared json object.
        JsonObject content_object = json_object.getAsJsonObject("content");

        //Loops through pets.
        this.content.forEach((key, value) -> {
            //Handles existing pets.
            if (content_object.keySet().contains(key.toString()))
                value.update(content_object.getAsJsonObject(key.toString())); //If pet is exist in the content json object.
            else
                this.remove(key); //Deletes player pet since it is no longer in the inventory.
        });

        //Handles new pets.
        content_object.keySet()
                .stream()
                .filter(pet_uid_string -> this.find(UUID.fromString(pet_uid_string)).isEmpty()).forEach(pet_uid_string -> {
                    //Declares pet uid.
                    @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);
                    //Creates new player pet object then adds to the content list.
                    this.content.put(pet_uid, new PlayerPet(this.player, pet_uid, content_object.getAsJsonObject(pet_uid_string)));
                });
    }
}

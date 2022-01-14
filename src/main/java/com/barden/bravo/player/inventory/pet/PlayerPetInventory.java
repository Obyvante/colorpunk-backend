package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Player pet inventory class.
 */
public final class PlayerPetInventory extends MetadataEntity {

    private final Player player;
    private final PlayerInventory inventory;
    private final HashSet<PlayerPet> content = new HashSet<>();
    private HashSet<PlayerPet> actives = new HashSet<>();

    /**
     * Creates player pet inventory object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");
    }

    /**
     * Creates player pet inventory object.
     *
     * @param player       Player.
     * @param inventory    Player inventory.
     * @param bsonDocument Bson document. (MONGO)
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "player pet inventory bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");

        //Gets pet content.
        @Nonnull BsonDocument pets_content = Objects.requireNonNull(bsonDocument.getDocument("content"), "player pet inventory content bson document cannot be null!");
        //Loops pet bson documents.
        pets_content.keySet().forEach(pet_uid_string -> {
            //Declares required fields.
            @Nonnull BsonDocument pet_document = Objects.requireNonNull(pets_content.getDocument(pet_uid_string), "player pet bson document cannot be null!");
            int pet_id = Objects.requireNonNull(pet_document.getInt32("id"), "pet id cannot be null!").intValue();
            boolean pet_active = pet_document.getBoolean("active").getValue();
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);

            //Creates new player pet then adds to the pets list.
            this.content.add(new PlayerPet(this.player, this, pet_id, pet_uid, pet_active));
        });

        //Refreshes actives.
        this.refreshActives();
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
     * Gets player inventory.
     *
     * @return Player inventory.
     */
    @Nonnull
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets pets.
     *
     * @return Player pets.
     */
    @Nonnull
    public HashSet<PlayerPet> getContent() {
        return this.content;
    }

    /**
     * Gets pets.
     *
     * @return Player pets. (SAFE)
     */
    @Nonnull
    public HashSet<PlayerPet> getContentSafe() {
        return new HashSet<>(this.content);
    }

    /**
     * Finds pet. (SAFE)
     *
     * @param uid Player pet unique id.
     * @return Optional player pet.
     */
    @Nonnull
    public Optional<PlayerPet> find(@Nonnull UUID uid) {
        return this.content.stream().filter(pet -> pet.getUID().equals(Objects.requireNonNull(uid, "unique id cannot be null!"))).findFirst();
    }

    /**
     * Gets pet. (UNSAFE)
     *
     * @param uid Player pet unique id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet get(@Nonnull UUID uid) {
        return this.find(uid).orElseThrow(() -> new NullPointerException("player pet cannot be null!"));
    }

    /**
     * Creates new pet.
     *
     * @param id Pet id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet create(int id) {
        //Creates new pet.
        PlayerPet pet = new PlayerPet(this.player, this, id, UUID.randomUUID(), false);
        //Adds created pet to the pets list.
        this.content.add(pet);
        //Returns created pet.
        return pet;
    }

    /**
     * Adds new pet to the pets list.
     *
     * @param player_pet Player pet.
     */
    public void add(@Nonnull PlayerPet player_pet) {
        //Adds pet to the pets list.
        this.content.add(Objects.requireNonNull(player_pet, "player pet cannot be null!"));
        //Updates player pet for player pet storage.
        this.updateActive(player_pet);
    }

    /**
     * Removes new pet to from pets list.
     *
     * @param player_pet Player pet.
     */
    public void remove(@Nonnull PlayerPet player_pet) {
        //Removes pet from the pets list.
        this.content.remove(player_pet);
        //Updates player pet for player pet storage.
        this.updateActive(player_pet);
    }


    /**
     * Gets active player pets.
     *
     * @return Active player pets.
     */
    @Nonnull
    public HashSet<PlayerPet> getActives() {
        return this.actives;
    }

    /**
     * Gets active player pets.
     *
     * @return Active player pets. (SAFE)
     */
    @Nonnull
    public HashSet<PlayerPet> getActivesSafe() {
        return new HashSet<>(this.actives);
    }

    /**
     * Updates player pet active status.
     *
     * @param player_pet Player pet.
     */
    public void updateActive(@Nonnull PlayerPet player_pet) {
        //Objects null check.
        Objects.requireNonNull(player_pet, "player pet cannot be null!");

        //If player pet is removed, no need to continue.
        if (this.find(player_pet.getUID()).isEmpty()) {
            this.actives.remove(player_pet);
            return;
        }

        //Handles player pet path in the storage.
        if (player_pet.isActive())
            this.actives.add(player_pet);
        else
            this.actives.remove(player_pet);
    }

    /**
     * Refreshes actives pets list.
     */
    public void refreshActives() {
        this.actives = this.getContent().stream().filter(PlayerPet::isActive).collect(Collectors.toCollection(HashSet::new));
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
        JsonObject pets_json_object = new JsonObject();
        //Loops through pets and adds one by one to the created json object.
        this.content.forEach(pet -> pets_json_object.add(pet.getUID().toString(), pet.toJsonObject()));
        //Adds pet(s) json object to the base json object.
        json_object.add("content", pets_json_object);

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
        BsonDocument bson_document = new BsonDocument();

        //Creates content bson document.
        BsonDocument content_document = new BsonDocument();
        //Loops through content and add pets one by one to the content bson document.
        this.content.forEach(pet -> content_document.put(pet.getUID().toString(), pet.toBsonDocument()));
        //Puts content bson document to the base document.
        bson_document.put("content", content_document);

        //Returns created bson document.
        return bson_document;
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
        JsonObject content_json_object = json_object.getAsJsonObject("content");

        //Loops through pets.
        this.getContentSafe().forEach(pet -> {
            //Handles existing pets.
            if (content_json_object.keySet().contains(pet.getUID().toString()))
                pet.update(content_json_object.getAsJsonObject(pet.getUID().toString())); //If pet is exist in the content json object.
            else
                pet.delete(); //Deletes pet since it is no longer in the inventory.
        });

        //Handles new pets.
        content_json_object.keySet().stream().filter(pet_uid_string -> this.find(UUID.fromString(pet_uid_string)).isEmpty()).forEach(pet_uid_string -> {
            //Declares pet uid.
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);
            //Creates new player pet object then adds to the content list.
            this.content.add(new PlayerPet(this.player, this, pet_uid, content_json_object.getAsJsonObject(pet_uid_string)));
        });
    }
}

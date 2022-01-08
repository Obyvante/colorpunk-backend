package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bson.Document;

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

    private HashSet<PlayerPet> content = new HashSet<>();
    private HashSet<UUID> actives = new HashSet<>();

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
     * @param player    Player.
     * @param inventory Player inventory.
     */
    public PlayerPetInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "player pet inventory document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");

        //Gets pet content.
        @Nonnull Document pets_content = Objects.requireNonNull(document.get("content", Document.class), "content cannot be null!");
        //Loops pet documents.
        pets_content.keySet().forEach(pet_uid_string -> {
            //Declares required fields.
            @Nonnull Document pet_document = Objects.requireNonNull(pets_content.get(pet_uid_string, Document.class), "pet document cannot be null!");
            int pet_id = Objects.requireNonNull(pet_document.getInteger("id"), "pet id cannot be null!");
            @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);

            //Creates new player pet then adds to the pets list.
            this.content.add(new PlayerPet(this.player, this, pet_id, pet_uid));
        });

        //Gets active pets from the field.
        this.actives = new HashSet<>(document.getList("actives", UUID.class));
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
     * Gets pets. (SAFE)
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
        PlayerPet pet = new PlayerPet(this.player, this, id, UUID.randomUUID());
        //Adds created pet to the pets list.
        this.content.add(pet);
        //Returns created pet.
        return pet;
    }

    /**
     * Adds new pet to the pets list.
     *
     * @param pet Player pet.
     */
    public void add(@Nonnull PlayerPet pet) {
        //Adds pet to the pets list.
        this.content.add(Objects.requireNonNull(pet, "player pet cannot be null!"));
    }

    /**
     * Removes new pet to from pets list.
     *
     * @param pet Player pet.
     */
    public void remove(@Nonnull PlayerPet pet) {
        //Removes pet from the pets list.
        this.removeActive(Objects.requireNonNull(pet, "player pet cannot be null!"));
        this.content.remove(pet);
    }

    /**
     * Gets active pets.
     *
     * @return Active pets.
     */
    @Nonnull
    public HashSet<UUID> getActives() {
        return this.actives;
    }

    /**
     * Gets active pets as player pet.
     *
     * @return Active pets as player pet.
     */
    @Nonnull
    public HashSet<PlayerPet> getActivesAsPet() {
        return this.actives.stream().map(this::get).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Finds active pet. (SAFE)
     *
     * @param uid Player pet unique id.
     * @return Optional player pet.
     */
    @Nonnull
    public Optional<PlayerPet> findActive(@Nonnull UUID uid) {
        return this.getActivesAsPet().stream().filter(pet -> pet.getUID().equals(Objects.requireNonNull(uid, "unique id cannot be null!"))).findFirst();
    }

    /**
     * Gets active pet. (UNSAFE)
     *
     * @param uid Player pet unique id.
     * @return Player pet.
     */
    @Nonnull
    public PlayerPet getActive(@Nonnull UUID uid) {
        return this.findActive(uid).orElseThrow(() -> new NullPointerException("player pet cannot be null!"));
    }

    /**
     * Adds pet to the active pets list.
     *
     * @param pet Player pet.
     */
    public void addActive(@Nonnull PlayerPet pet) {
        this.actives.add(Objects.requireNonNull(pet, "pet cannot be null!").getUID());
    }

    /**
     * Removes pet from the active pets list.
     *
     * @param pet Player pet.
     */
    public void removeActive(@Nonnull PlayerPet pet) {
        this.actives.remove(Objects.requireNonNull(pet, "pet cannot be null!").getUID());
    }

    /**
     * Resets active pets.
     */
    public void resetActive() {
        this.actives = new HashSet<>();
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

        //Creates new active pets json array object.
        JsonArray actives_array = new JsonArray();
        //Loops through active pets and adds one by one to the created json array object.
        this.actives.forEach(active -> actives_array.add(active.toString()));
        //Adds active pets json array object to the base json object.
        json_object.add("actives", actives_array);

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
     * Converts player pet inventory object to document. (MONGO BSON)
     *
     * @return Player pet inventory document.
     */
    @Nonnull
    public Document toDocument() {
        //Creates empty document.
        Document document = new Document();

        //Adds active pets to the json.
        document.put("actives", this.actives);

        //Creates content document.
        Document content_document = new Document();
        //Loops through content and add pets one by one to the content document.
        this.content.forEach(pet -> content_document.put(pet.getUID().toString(), pet.toDocument()));

        //Puts content document to the base document.
        document.put("content", content_document);

        //Returns created document.
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

        //Resets active pets list.
        this.actives = new HashSet<>();
        //Loops through json array and add active ones one by one to the rested active pet list.
        json_object.getAsJsonArray("actives").forEach(active -> this.actives.add(UUID.fromString(active.getAsString())));

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
        content_json_object.keySet().stream()
                .filter(pet_uid_string -> this.findActive(UUID.fromString(pet_uid_string)).isEmpty())
                .forEach(pet_uid_string -> {
                    //Declares pet uid.
                    @Nonnull UUID pet_uid = UUID.fromString(pet_uid_string);
                    //Creates new player pet object then adds to the content list.
                    this.content.add(new PlayerPet(this.player, this, pet_uid, content_json_object.getAsJsonObject(pet_uid_string)));
                });
    }
}

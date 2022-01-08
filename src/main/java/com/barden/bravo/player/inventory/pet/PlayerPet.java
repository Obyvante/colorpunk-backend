package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.pet.Pet;
import com.barden.bravo.pet.PetRepository;
import com.barden.bravo.player.Player;
import com.barden.library.metadata.MetadataEntity;
import com.google.gson.JsonObject;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

/**
 * Player pet class.
 */
public final class PlayerPet extends MetadataEntity {

    private final Player player;
    private final PlayerPetInventory inventory;

    private final int id;
    private final UUID uid;

    /**
     * Creates player pet object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     * @param id        Pet id.
     * @param uid       Pet unique id.
     */
    public PlayerPet(@Nonnull Player player, @Nonnull PlayerPetInventory inventory, int id, @Nonnull UUID uid) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player pet inventory cannot be null!");
        this.id = id;
        this.uid = Objects.requireNonNull(uid, "uid cannot be null!");
    }

    /**
     * Creates player pet object from json object.
     *
     * @param player      Player.
     * @param inventory   Player inventory.
     * @param uid         Pet unique id.
     * @param json_object Player pet json object.
     */
    public PlayerPet(@Nonnull Player player, @Nonnull PlayerPetInventory inventory, @Nonnull UUID uid, @Nonnull JsonObject json_object) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player pet inventory cannot be null!");
        this.uid = Objects.requireNonNull(uid, "uid cannot be null!");
        this.id = json_object.get("id").getAsInt();
    }

    /**
     * Gets pet.
     *
     * @return Pet.
     */
    @Nonnull
    public Pet getPet() {
        return PetRepository.get(this.id);
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
     * Gets pet inventory.
     *
     * @return Player pet inventory.
     */
    @Nonnull
    public PlayerPetInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets id.
     *
     * @return Pet id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets UID.
     *
     * @return Pet unique id.
     */
    @Nonnull
    public UUID getUID() {
        return this.uid;
    }

    /**
     * Gets if pet is active or not.
     *
     * @return If player pet is active or not.
     */
    public boolean isActive() {
        return this.inventory.getActives().contains(this.uid);
    }

    /**
     * Sets pet status.
     *
     * @param status Player pet status. (TRUE = active, FALSE = inactive)
     */
    public void setActive(boolean status) {
        if (status) {
            //If it is already active, no need to continue.
            if (this.isActive())
                return;
            //Adds "this" to the active player pets list.
            this.inventory.addActive(this);
        } else {
            //If it is already inactive, no need to continue.
            if (!this.isActive())
                return;
            //Removes "this" from the active player pets list.
            this.inventory.removeActive(this);
        }
    }

    /**
     * Deletes player pet.
     */
    public void delete() {
        this.inventory.remove(this);
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player pet as a json object.
     *
     * @return Player pet as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures fields.
        json_object.addProperty("id", this.id);

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player pet object to document. (MONGO BSON)
     *
     * @return Player pet document.
     */
    @Nonnull
    public Document toDocument() {
        //Creates empty document.
        Document document = new Document();

        //Sets base fields.
        document.put("id", this.id);

        //Returns created document.
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player pet object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player pet json object cannot be null!");

        //Nothing for now.
    }
}

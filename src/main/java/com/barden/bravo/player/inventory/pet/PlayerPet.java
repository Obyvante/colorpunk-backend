package com.barden.bravo.player.inventory.pet;

import com.barden.bravo.player.Player;
import com.barden.library.metadata.MetadataEntity;
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
        document.put("uid", this.uid);

        //Returns created document.
        return document;
    }
}

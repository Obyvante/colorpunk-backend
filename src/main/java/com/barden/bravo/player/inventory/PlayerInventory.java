package com.barden.bravo.player.inventory;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.pet.PlayerPetInventory;
import com.barden.library.metadata.MetadataEntity;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Player inventory class.
 */
public final class PlayerInventory extends MetadataEntity {

    private final Player player;
    private final PlayerPetInventory petInventory;

    /**
     * Creates player inventory object.
     *
     * @param player Player.
     */
    public PlayerInventory(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.petInventory = new PlayerPetInventory(this.player, this);
    }

    /**
     * Creates player inventory object.
     *
     * @param player   Player.
     * @param document Mongo document. (INVENTORY BSON)
     */
    public PlayerInventory(@Nonnull Player player, @Nonnull Document document) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.petInventory = new PlayerPetInventory(this.player, this, Objects.requireNonNull(document.get("pets", Document.class), "pets document cannot be null!"));
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
    public PlayerPetInventory getPet() {
        return this.petInventory;
    }
}

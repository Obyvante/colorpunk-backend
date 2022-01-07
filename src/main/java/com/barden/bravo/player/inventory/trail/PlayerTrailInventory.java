package com.barden.bravo.player.inventory.trail;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.bravo.trail.Trail;
import com.barden.library.metadata.MetadataEntity;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * Player trail inventory abstract class.
 */
public abstract class PlayerTrailInventory extends MetadataEntity {

    private final Player player;
    private final PlayerInventory inventory;

    private HashSet<Integer> content = new HashSet<>();
    private int active;

    /**
     * Creates player trail inventory object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     */
    public PlayerTrailInventory(Player player, PlayerInventory inventory) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");
    }

    /**
     * Gets player.
     *
     * @return Player.
     */
    @Nonnull
    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Gets player inventory.
     *
     * @return Player inventory.
     */
    @Nonnull
    public final PlayerInventory getInventory() {
        return this.inventory;
    }

    /**
     * Gets trails.
     *
     * @return Trails.
     */
    @Nonnull
    public final HashSet<Integer> getContent() {
        return this.content;
    }

    /**
     * Checks if inventory has declared trail or not.
     *
     * @param trail Trail.
     * @return If inventory has declared trail or not.
     */
    public final boolean has(@Nonnull Trail trail) {
        return this.content.contains(Objects.requireNonNull(trail, "trail cannot be null!").getId());
    }

    /**
     * Adds trail to the content.
     *
     * @param trail Trail.
     */
    public final void add(@Nonnull Trail trail) {
        this.content.add(Objects.requireNonNull(trail, "trail cannot be null!").getId());
    }

    /**
     * Removes trail from the content.
     *
     * @param trail Trail.
     */
    public final void remove(@Nonnull Trail trail) {
        this.content.remove(Objects.requireNonNull(trail, "trail cannot be null!").getId());
    }

    /**
     * Gets active trail.
     *
     * @return Trail.
     */
    public final int getActive() {
        return this.active;
    }

    /**
     * Gets active trail as trail.
     *
     * @return Optional trail.
     */
    public final Optional<Trail> getActiveAsTrail() {
        return Optional.ofNullable(null);
    }
}

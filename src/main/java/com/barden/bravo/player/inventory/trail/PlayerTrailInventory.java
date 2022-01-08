package com.barden.bravo.player.inventory.trail;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.inventory.PlayerInventory;
import com.barden.bravo.trail.Trail;
import com.barden.bravo.trail.TrailRepository;
import com.barden.bravo.trail.TrailType;
import com.barden.library.metadata.MetadataEntity;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * Player trail inventory abstract class.
 */
public final class PlayerTrailInventory extends MetadataEntity {

    private final Player player;
    private final PlayerInventory inventory;

    private HashSet<Integer> content = new HashSet<>();
    private HashMap<TrailType, Integer> actives;

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
     * Creates player trail inventory object.
     *
     * @param player    Player.
     * @param inventory Player inventory.
     * @param document  Mongo document. (INVENTORY BSON)
     */
    public PlayerTrailInventory(@Nonnull Player player, @Nonnull PlayerInventory inventory, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "player trail inventory document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        this.inventory = Objects.requireNonNull(inventory, "player inventory cannot be null!");
        this.content = new HashSet<>(document.getList("content", Integer.class));

        //Declares actives.
        Objects.requireNonNull(document.get("actives", Document.class), "actives document cannot be null!").forEach(
                (key, value) -> this.actives.put(TrailType.valueOf(key), (int) value));
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
     * Gets trails.
     *
     * @return Trails.
     */
    @Nonnull
    public HashSet<Integer> getContent() {
        return this.content;
    }

    /**
     * Checks if inventory has declared trail or not.
     *
     * @param trail Trail.
     * @return If inventory has declared trail or not.
     */
    public boolean has(@Nonnull Trail trail) {
        return this.content.contains(Objects.requireNonNull(trail, "trail cannot be null!").getId());
    }

    /**
     * Adds trail to the content.
     *
     * @param trail Trail.
     */
    public void add(@Nonnull Trail trail) {
        this.content.add(Objects.requireNonNull(trail, "trail cannot be null!").getId());
    }

    /**
     * Removes trail from the content.
     *
     * @param trail Trail.
     */
    public void remove(@Nonnull Trail trail) {
        if (this.isActive(trail))
            this.actives.remove(trail.getType());
        this.content.remove(Objects.requireNonNull(trail, "trail cannot be null!").getId());
    }

    /**
     * Gets active trails.
     *
     * @return Active trails.
     */
    @Nonnull
    public HashSet<TrailType> getActives() {
        return new HashSet<>(this.actives.keySet());
    }

    /**
     * Gets active trail id.
     *
     * @return Active trail id.
     */
    public int getActive(@Nonnull TrailType type) {
        return this.actives.getOrDefault(Objects.requireNonNull(type, "type cannot be null!"), -1);
    }

    /**
     * Finds active trail by its id.
     *
     * @return Optional trail.
     */
    @Nonnull
    public Optional<Trail> findActiveAsTrail(@Nonnull TrailType type) {
        return TrailRepository.find(this.getActive(type));
    }

    /**
     * Gets active trail by its id.
     *
     * @return Trail.
     */
    @Nonnull
    public Trail getActiveAsTrail(@Nonnull TrailType type) {
        return TrailRepository.get(this.getActive(type));
    }

    /**
     * Checks if trail is active or not.
     *
     * @param trail Trail.
     * @return If trail is active or not.
     */
    public boolean isActive(@Nonnull Trail trail) {
        //Objects null check.
        Objects.requireNonNull(trail, "trail cannot be null!");

        //If trail is not inside the inventory, no need to continue.
        if (this.has(trail))
            return false;

        //Returns if active trail is same as declared one.
        return this.getActive(trail.getType()) == trail.getId();
    }

    /**
     * Resets active trails.
     */
    public void resetActive() {
        this.actives = new HashMap<>();
    }
}

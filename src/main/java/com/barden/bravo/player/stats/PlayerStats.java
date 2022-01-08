package com.barden.bravo.player.stats;

import com.barden.bravo.player.Player;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Player stats class.
 */
public final class PlayerStats {

    private final Player player;
    private final HashMap<PlayerStatType, Double> stats = new HashMap<>();

    /**
     * Creates player stats object.
     *
     * @param player Player.
     */
    public PlayerStats(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player stats object.
     *
     * @param player   Player.
     * @param document Mongo document. (STATS BSON)
     */
    public PlayerStats(@Nonnull Player player, @Nonnull Document document) {
        //Objects null check.
        Objects.requireNonNull(document, "stats document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");
        //Declares stats.
        document.forEach((key, value) -> this.stats.put(PlayerStatType.valueOf(key), (double) value));
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
     * Gets stat value.
     *
     * @param type Player stat type.
     * @return Player stat value.
     */
    public double get(@Nonnull PlayerStatType type) {
        return this.stats.getOrDefault(Objects.requireNonNull(type, "type cannot be null!"), 0.0d);
    }

    /**
     * Adds value to stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void add(@Nonnull PlayerStatType type, double value) {
        this.stats.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) + value, 0));
    }

    /**
     * Removes value from stat.
     * NOTE: Stat value cannot be negative.
     *
     * @param type  Player stat type.
     * @param value Value. (POSITIVE NUMBER)
     */
    public void remove(@Nonnull PlayerStatType type, double value) {
        this.stats.put(Objects.requireNonNull(type, "type cannot be null!"), Math.max(this.get(type) - value, 0));
    }
}

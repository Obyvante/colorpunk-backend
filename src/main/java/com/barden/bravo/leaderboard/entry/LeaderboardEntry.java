package com.barden.bravo.leaderboard.entry;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Leaderboard entry class.
 */
public final class LeaderboardEntry {

    private final long id;
    private final String name;
    private final double score;
    private final long position;

    /**
     * Creates a leaderboard entry.
     *
     * @param id       Roblox user id.
     * @param name     Roblox user name.
     * @param score    Entry score.
     * @param position Entry position. (Leaderboard position)
     */
    public LeaderboardEntry(long id, @Nonnull String name, double score, long position) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.score = score;
        this.position = position;
    }

    /**
     * Gets roblox user id.
     *
     * @return Roblox user id.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets player name.
     *
     * @return Player name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Gets entry score.
     *
     * @return Entry score.
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Get entry position.
     *
     * @return Entry position. (Leaderboard position.)
     */
    public long getPosition() {
        return this.position;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets leaderboard entry as a json object.
     *
     * @return Leaderboard entry as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configure fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("name", this.name);
        json_object.addProperty("score", this.score);

        //Returns created json object.
        return json_object;
    }
}

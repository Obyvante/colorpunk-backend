package com.barden.bravo.leaderboard.user;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

/**
 * Leaderboard user class.
 */
public final class LeaderboardUser {

    private final long id;
    private final double score;
    private final long position;

    /**
     * Creates leaderboard user.
     *
     * @param id       Roblox user id.
     * @param score    User score.
     * @param position User position. (Leaderboard position)
     */
    public LeaderboardUser(long id, double score, long position) {
        this.id = id;
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
     * Gets user score.
     *
     * @return User score.
     */
    public double getScore() {
        return this.score;
    }

    /**
     * Get user position.
     *
     * @return User position. (Leaderboard position.)
     */
    public long getPosition() {
        return this.position;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets leaderboard user as a json object.
     *
     * @return Leaderboard user as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("score", this.score);

        //Returns created json object.
        return json_object;
    }
}

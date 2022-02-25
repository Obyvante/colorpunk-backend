package com.barden.bravo.leaderboard;

import com.barden.bravo.leaderboard.type.LeaderboardType;
import com.barden.bravo.leaderboard.user.LeaderboardUser;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Leaderboard class.
 */
public final class Leaderboard {

    private final LeaderboardType type;
    private Collection<LeaderboardUser> users = new ArrayList<>();
    private final int size;

    /**
     * Creates leaderboard
     *
     * @param type Leaderboard type.
     * @param size Leaderboard user size.
     */
    public Leaderboard(@Nonnull LeaderboardType type, int size) {
        this.type = Objects.requireNonNull(type, "type cannot be null!");
        this.size = size;
        //Updates leaderboard.
        this.update();
    }

    /**
     * Gets leaderboard type.
     *
     * @return Leaderboard type.
     */
    @Nonnull
    public LeaderboardType getType() {
        return this.type;
    }

    /**
     * Gets leaderboard users.
     *
     * @return Leaderboard users.
     */
    @Nonnull
    public Collection<LeaderboardUser> getUsers() {
        return this.users;
    }

    /**
     * Gets leaderboard user size.
     *
     * @return Leaderboard user size.
     */
    public int getSize() {
        return this.size;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets leaderboard as a json object.
     *
     * @return Leaderboard as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures user fields.
        this.users.forEach(user -> json_object.add(String.valueOf(user.getPosition()), user.toJsonObject()));

        //Returns created json object.
        return json_object;
    }


    /*
    DATABASE
     */

    /**
     * Updates leaderboard.
     */
    public void update() {
        //Resets users.
        this.users = new ArrayList<>();

        //Handles redis exception.
        try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
            //Gets leaderboard.
            List<Tuple> tuples = resource.zrevrangeWithScores("leaderboard:" + this.type.name(), 0, this.size - 1);

            //Declares base fields.
            long position = 0;

            //Loops through leaderboard tuple.
            for (@Nonnull Tuple tuple : tuples) {
                //Increases position.
                position++;

                //Adds tuple user to the users list.
                this.users.add(new LeaderboardUser(Long.parseLong(tuple.getElement()), tuple.getScore(), position));
            }
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't update leaderboard(" + this.type.name() + ")!", exception);
        }
    }
}

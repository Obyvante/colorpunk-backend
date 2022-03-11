package com.barden.bravo.leaderboard;

import com.barden.bravo.leaderboard.entry.LeaderboardEntry;
import com.barden.bravo.statistics.type.StatisticType;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.resps.Tuple;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Leaderboard class.
 */
public final class Leaderboard {

    private final StatisticType type;
    private final BiMap<Long, LeaderboardEntry> users;
    private final int size;

    /**
     * Creates leaderboard
     *
     * @param type Statistic type.
     * @param size Leaderboard user size.
     */
    public Leaderboard(@Nonnull StatisticType type, int size) {
        this.type = Objects.requireNonNull(type, "type cannot be null!");
        this.users = HashBiMap.create(size);
        this.size = size;
        //Updates leaderboard.
        this.update();
    }

    /**
     * Gets statistic type.
     *
     * @return Statistic type.
     */
    @Nonnull
    public StatisticType getType() {
        return this.type;
    }

    /**
     * Gets leaderboard users.
     *
     * @return Leaderboard users.
     */
    @Nonnull
    public Set<LeaderboardEntry> getUsers() {
        return this.users.values();
    }

    /**
     * Gets leaderboard user size.
     *
     * @return Leaderboard user size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Gets player rank. (SYNC)
     *
     * @param id Roblox user id.
     * @return Player rank.
     */
    public long getPlayerRank(long id) {
        try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
            Long rank = resource.zrevrank("leaderboard:" + this.type, String.valueOf(id));
            if (rank == null)
                return -1;
            return resource.zrevrank("leaderboard:" + this.type, String.valueOf(id)) + 1;
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't get player(" + id + ") rank for leaderboard(" + this.type.name() + ")!");
        }
        return -1;
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
        this.users.values().forEach(user -> json_object.add(String.valueOf(user.getPosition()), user.toJsonObject()));

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
        this.users.clear();

        //Handles redis exception.
        try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
            //Gets leaderboard.
            List<Tuple> tuples = resource.zrevrangeWithScores("leaderboard:" + this.type.name(), 0, this.size - 1);

            //Creates pipeline.
            Pipeline pipeline = resource.pipelined();

            //Saves player to list to fetch their data.
            HashMap<String, Response<Map<String, String>>> players = new HashMap<>();
            tuples.forEach(tuple -> players.put(tuple.getElement(), pipeline.hgetAll("player:" + tuple.getElement())));

            //Gets and closes pipeline.
            pipeline.sync();

            //Declares base fields.
            long position = 0;

            //Loops through leaderboard tuple.
            for (@Nonnull Tuple tuple : tuples) {
                //Increases position.
                position++;

                //Declares required fields.
                Map<String, String> _data = players.get(tuple.getElement()).get();
                if (_data == null || _data.isEmpty())
                    continue;
                String user_name = _data.get("name");
                long user_id = Long.parseLong(tuple.getElement());

                //Adds tuple user to the users list.
                this.users.put(user_id, new LeaderboardEntry(user_id, user_name, tuple.getScore(), position));
            }
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't update leaderboard(" + this.type.name() + ")!", exception);
        }
    }
}

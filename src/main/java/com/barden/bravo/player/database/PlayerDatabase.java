package com.barden.bravo.player.database;

import com.barden.bravo.database.DatabaseStructure;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerProvider;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import redis.clients.jedis.Jedis;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * Player database class.
 * This class is mostly for database calls and gets. It also has useful
 * database methods/functions to make code shorter and safer.
 */
public final class PlayerDatabase extends DatabaseStructure<Player, PlayerBsonField> {

    /**
     * Creates a player database.
     *
     * @param player Player.
     */
    public PlayerDatabase(@Nonnull Player player) {
        super(player, PlayerBsonField.class, PlayerProvider.getMongoProvider());
    }

    /**
     * Saves player redis fields.
     */
    public void saveRedis() {
        try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
            //Declares required fields.
            String id_string = String.valueOf(this.parent.getId());
            HashMap<String, String> _data = new HashMap<>();

            //Sets base fields for hash map.
            _data.put("id", id_string);
            _data.put("name", this.parent.getName());

            //Saves hash map with id of player.
            resource.hset("player:" + this.parent.getId(), _data);
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't update leaderboard(" + this.parent + ")!", exception);
        }
    }

    /**
     * Saves player redis fields. (ASYNC)
     */
    public void saveRedisAsync() {
        this.saveRedis();
    }
}

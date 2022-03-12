package com.barden.bravo.leaderboard;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerProvider;
import com.barden.bravo.player.statistics.PlayerStatistics;
import com.barden.bravo.player.statistics.type.PlayerStatisticType;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.scheduler.SchedulerProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Leaderboard provider class.
 */
public final class LeaderboardProvider {

    private static final BiMap<PlayerStatisticType, Leaderboard> content = HashBiMap.create();

    /**
     * Initializes leaderboard provider class.
     */
    public static void initialize() {
        //Initializes leaderboards by statistic types.
        for (PlayerStatisticType type : PlayerStatisticType.values())
            content.put(type, new Leaderboard(type, 100));

        //Handles scheduler to update leaderboards.
        SchedulerProvider.create().every(30, TimeUnit.SECONDS).schedule(task -> {
            //Updates leaderboard scores.
            LeaderboardProvider.update(PlayerProvider.getContent());

            //Updates leaderboard.
            content.values().forEach(Leaderboard::update);
        });
    }

    /**
     * Gets leaderboard by its type.
     *
     * @param type Statistic type.
     * @return Leaderboard.
     */
    @Nonnull
    public static Leaderboard get(@Nonnull PlayerStatisticType type) {
        //Object null checks.
        Objects.requireNonNull(type, "type cannot be null!");
        return Objects.requireNonNull(content.get(type), "leaderboard(" + type.name() + ") cannot be null!");
    }

    /**
     * Updates player leaderboard scores. (REDIS) (SYNC)
     *
     * @param players Players.
     */
    public static void update(@Nonnull Set<Player> players) {
        //Objects null check.
        Objects.requireNonNull(players, "players cannot be null!");

        //If player is empty, no need to continue.
        if (players.isEmpty())
            return;

        //Handles database update. (REDIS) [LEADERBOARD]
        try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
            //Creates pipeline.
            Pipeline pipeline = resource.pipelined();

            //Updates leaderboard fields.
            for (Player player : players) {
                //Gets player statistics.
                PlayerStatistics statistics = player.getStatistics();

                //Saves player statistics to leaderboard.
                Arrays.stream(PlayerStatisticType.values()).forEach(type ->
                        pipeline.zadd("leaderboard:" + type.name(), statistics.get(type), String.valueOf(player.getId())));
            }

            //Executes pipeline.
            pipeline.sync();
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't updater player leaderboard!", exception);
        }
    }
}

package com.barden.bravo.leaderboard;

import com.barden.bravo.leaderboard.enums.LeaderboardType;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerRepository;
import com.barden.bravo.player.statistics.PlayerStatistics;
import com.barden.bravo.statistics.enums.StatisticType;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseRepository;
import com.barden.library.scheduler.SchedulerRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Leaderboard repository class.
 */
public final class LeaderboardRepository {

    private static final Collection<Leaderboard> leaderboards = new ArrayList<>();

    /**
     * Initializes leaderboard repository class.
     */
    public static void initialize() {
        //Initializes leaderboards by types.
        Arrays.stream(LeaderboardType.values()).forEach(type -> leaderboards.add(new Leaderboard(type, 100)));

        //Handles scheduler to update leaderboards.
        SchedulerRepository.create().every(1, TimeUnit.MINUTES).schedule(task -> {
            //Updates leaderboard scores.
            LeaderboardRepository.update(PlayerRepository.getContent());

            //Updates leaderboard.
            leaderboards.forEach(Leaderboard::update);
        });
    }

    /**
     * Gets leaderboard by its type.
     *
     * @param type Leaderboard type.
     * @return Leaderboard.
     */
    @Nonnull
    public static Leaderboard get(@Nonnull LeaderboardType type) {
        return leaderboards.stream()
                .filter(leaderboard -> leaderboard.getType() == Objects.requireNonNull(type, "type cannot be null!"))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("leaderboard(" + type.name() + ") cannot be null!"));
    }


    /**
     * Updates player leaderboard scores. (REDIS) (SYNC)
     *
     * @param players Players.
     */
    public static void update(@Nonnull Collection<Player> players) {
        //Objects null check.
        Objects.requireNonNull(players, "players cannot be null!");

        //If player is empty, no need to continue.
        if (players.isEmpty())
            return;

        //Handles database update. (REDIS) [LEADERBOARD]
        try (Jedis resource = DatabaseRepository.redis().getClient().getResource()) {
            //Creates pipeline.
            Pipeline pipeline = resource.pipelined();

            //Updates leaderboard fields.
            for (Player player : players) {
                //Gets player statistics.
                PlayerStatistics statistics = player.getStatistics();

                //Saves player statistics to leaderboard.
                Arrays.stream(StatisticType.values()).forEach(type -> pipeline.zadd("leaderboard:" + type.name(), statistics.get(type), String.valueOf(player.getId())));
            }

            //Executes pipeline.
            pipeline.sync();
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't updater player leaderboard!", exception);
        }
    }
}

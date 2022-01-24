package com.barden.bravo;

import com.barden.bravo.cosmetics.pet.PetRepository;
import com.barden.bravo.cosmetics.trail.TrailRepository;
import com.barden.bravo.player.PlayerRepository;
import com.barden.bravo.settings.Settings;
import com.barden.bravo.statistics.StatisticsRepository;
import com.barden.bravo.leaderboard.LeaderboardRepository;
import com.barden.library.BardenJavaLibrary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Project bravo class. (MAIN)
 */
@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        InfluxDbAutoConfiguration.class
})
public class ProjectBravo {

    private static boolean INITIALIZED = false;

    /**
     * Gets if server is initialized or not.
     *
     * @return If server is initialized or not.
     */
    public static boolean isInitialize() {
        return INITIALIZED;
    }

    /**
     * Runs project bravo.
     *
     * @param arguments Arguments.
     */
    public static void main(@NonNull String[] arguments) {
        //Runs spring application.
        SpringApplication.run(ProjectBravo.class, Objects.requireNonNull(arguments, "arguments cannot be null!"));

        //Initializes barden java library.
        BardenJavaLibrary.initialize();

        //Initializes settings.
        Settings.initialize();
        //Initializes pet repository.
        PetRepository.initialize();
        //Initializes trail repository.
        TrailRepository.initialize();

        //Initializes player repository.
        PlayerRepository.initialize();

        //Initializes statistics repository.
        StatisticsRepository.initialize();
        //Initializes leaderboard repository.
        LeaderboardRepository.initialize();

        //Changes initialized field.
        INITIALIZED = true;
    }
}

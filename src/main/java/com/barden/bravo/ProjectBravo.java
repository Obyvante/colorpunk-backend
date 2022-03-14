package com.barden.bravo;

import com.barden.bravo.cosmetics.pet.PetProvider;
import com.barden.bravo.cosmetics.trail.TrailProvider;
import com.barden.bravo.leaderboard.LeaderboardProvider;
import com.barden.bravo.player.PlayerProvider;
import com.barden.bravo.product.ProductProvider;
import com.barden.bravo.settings.Settings;
import com.barden.bravo.statistics.StatisticsProvider;
import com.barden.bravo.transaction.provider.TransactionProvider;
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

import javax.annotation.PreDestroy;
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

        //Initializes product provider.
        ProductProvider.initialize();
        //Initializes pet provider.
        PetProvider.initialize();
        //Initializes trail provider.
        TrailProvider.initialize();

        //Initializes player provider.
        PlayerProvider.initialize();

        //Initializes statistics provider.
        StatisticsProvider.initialize();
        //Initializes leaderboard provider.
        LeaderboardProvider.initialize();

        //Initializes transaction provider.
        TransactionProvider.initialize();

        //Changes initialized field.
        INITIALIZED = true;
    }

    /**
     * Runs on exit.
     */
    @PreDestroy
    public void onExit() {
        PlayerProvider.getMongoProvider().save(PlayerProvider.getContent());

        //Terminates barden java library.
        BardenJavaLibrary.terminate();
    }
}

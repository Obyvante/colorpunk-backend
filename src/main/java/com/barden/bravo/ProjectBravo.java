package com.barden.bravo;

import com.barden.bravo.pet.PetRepository;
import com.barden.bravo.settings.Settings;
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
        //Initializes pet.
        PetRepository.initialize();
    }
}

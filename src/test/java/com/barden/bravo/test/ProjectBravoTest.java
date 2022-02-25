package com.barden.bravo.test;

import com.barden.bravo.statistics.type.StatisticType;
import com.barden.library.BardenJavaLibrary;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.lang.NonNull;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Project bravo class. (MAIN) [TEST]
 */
@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        InfluxDbAutoConfiguration.class
})
public class ProjectBravoTest {

    /**
     * Runs project bravo as a test.
     *
     * @param arguments Arguments.
     */
    public static void main(@NonNull String[] arguments) {
        //Runs spring application.
        SpringApplication.run(ProjectBravoTest.class, Objects.requireNonNull(arguments, "arguments cannot be null!"));

        //Initializes barden java library.
        BardenJavaLibrary.initialize();

        //Creates flux string.
        String task_players = """
                option task = {
                    name: "Statistic Downsampling: Players",
                    every: 1d,
                    offset: 0m
                }
                                
                """;

        //Creates string builder for 'function' in 'reduce'.
        StringBuilder reduce_function = new StringBuilder("{")
                .append("user: r.user, ")
                .append("_time: now(), ")
                .append("_measurement: \"players\"");
        //Loops through all statistic types and add to 'function' one by one.
        Arrays.stream(StatisticType.values()).forEach(type -> {
            //Declares required fields.
            String field = type.name();
            String r_field = "r." + type.name();
            String accumulator_field = "accumulator." + type.name();
            String exists = "if exists " + r_field + " then " + accumulator_field + " + " + r_field + " else 0.0";

            //Appends all configurations.
            reduce_function.append(", ").append(field).append(": ").append(exists);
        });
        //Closes 'function' bracket.
        reduce_function.append("}");

        //Creates string builder for 'identity' in 'reduce'.
        StringBuilder reduce_identity = new StringBuilder("{")
                .append("user: \"\", ")
                .append("_time: now(), ")
                .append("_measurement: \"players\"");
        //Loops through all statistic types and add to 'identity' one by one.
        Arrays.stream(StatisticType.values()).forEach(type -> reduce_identity.append(", ").append(type.name()).append(": 0.0"));
        //Closes 'identity' bracket.
        reduce_identity.append("}");

        //Creates string builder for 'function' in 'reduce'.
        StringBuilder to_function = new StringBuilder("({")
                .append("user: r.user");
        //Loops through all statistic types and add to 'function' one by one.
        Arrays.stream(StatisticType.values()).forEach(type -> {
            //Declares required fields.
            String field = type.name();
            String r_field = "r." + type.name();
            //Appends all configurations.
            to_function.append(", ").append(field).append(": ").append(r_field);
        });
        //Closes 'function' bracket.
        to_function.append("})");

        //Adds remaining to the string. (PLAYER)
        task_players += Flux.from("test")
                .range(-1L, ChronoUnit.DAYS)
                .filter(Restrictions.measurement().equal("players"))
                .pivot(List.of("_time"), List.of("_field"), "_value")
                .groupBy("user")
                .reduce(reduce_function.toString(), reduce_identity.toString())
                .to("statistics", "barden", to_function.toString())
                .toString();

        //Prints created task string.
        BardenJavaLibrary.getLogger().info(task_players);
    }
}

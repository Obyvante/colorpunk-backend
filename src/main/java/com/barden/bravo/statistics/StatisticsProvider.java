package com.barden.bravo.statistics;

import com.barden.bravo.statistics.type.StatisticType;
import com.barden.bravo.statistics.updater.StatisticsUpdater;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.database.influx.InfluxProvider;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.TaskCreateRequest;
import com.influxdb.client.domain.TaskStatusType;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Statistics provider class.
 */
//TODO: will inspect.
public final class StatisticsProvider {

    public static final String INDEX = "statistics";
    public static final String INDEX_DAILY = "statistics:daily";

    /**
     * Initializes statistics provider object.
     */
    public static void initialize() {
        //Handles mongo.
        handleMongo();
        //Handles influx.
        handleInflux();

        //Initializes statistics updater.
        StatisticsUpdater.initialize();
    }

    /**
     * Handles mongo.
     */
    private static void handleMongo() {
        //Unique indexes.
        DatabaseProvider.mongo().createIndex(
                "bravo",
                "statistics",
                Indexes.ascending("type"),
                new IndexOptions().unique(true).background(true));
    }

    /**
     * Handles influx.
     */
    private static void handleInflux() {
        //Declares required fields.
        InfluxProvider provider = DatabaseProvider.influx();
        InfluxDBClient client = provider.getClient();

        //Creates statistics bucket. (INFINITE)
        if (provider.findBucketByName("statistics").isEmpty())
            client.getBucketsApi().createBucket("statistics", provider.getOrganizationId());

        //Creates statistics bucket. (DAILY)
        if (provider.findBucketByName("statistics:daily").isEmpty())
            client.getBucketsApi().createBucket("statistics:daily",
                    new BucketRetentionRules().everySeconds((int) TimeUnit.DAYS.toSeconds(1)),
                    provider.getOrganizationId());

        /*
        GROUP
         */

        //If there is already a task, deletes it.
        provider.findTaskByName("Statistic Downsampling: Group").ifPresent(task -> provider.getClient().getTasksApi().deleteTask(task));

        //Creates flux string.
        String task_option_group = """
                option task = {
                    name: "Statistic Downsampling: Group",
                    every: 1d,
                    offset: 0m
                }
                                
                """;

        //Adds remaining to the string. (GROUP)
        task_option_group += Flux.from(INDEX_DAILY)
                .range(-1L, ChronoUnit.DAYS)
                .filter(Restrictions.measurement().equal("game"))
                .aggregateWindow().withEvery("1d").withAggregateFunction("sum")
                .to(INDEX, provider.getOrganization(), """
                        ({"sum": r._value})
                         """)
                .toString();
        //Creates group task.
        client.getTasksApi().createTask(new TaskCreateRequest()
                .description("Downsampling statistics to daily statistics. (GROUP)")
                .status(TaskStatusType.ACTIVE)
                .orgID(provider.getOrganizationId())
                .flux(task_option_group));


        /*
        PLAYER
         */

        //If there is already a task, deletes it.
        provider.findTaskByName("Statistic Downsampling: Players").ifPresent(task -> provider.getClient().getTasksApi().deleteTask(task));

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

        //Adds remaining to the string. (PLAYER)
        task_players += Flux.from("test")
                .range(-1L, ChronoUnit.DAYS)
                .filter(Restrictions.measurement().equal("players"))
                .pivot(List.of("_time"), List.of("_field"), "_value")
                .groupBy("user")
                .reduce(reduce_function.toString(), reduce_identity.toString())
                .to(INDEX, provider.getOrganization())
                .toString();

        //Creates players task.
        client.getTasksApi().createTask(new TaskCreateRequest()
                .description("Downsampling statistics to daily statistics. (PLAYER)")
                .status(TaskStatusType.ACTIVE)
                .orgID(provider.getOrganizationId())
                .flux(task_players));
    }
}

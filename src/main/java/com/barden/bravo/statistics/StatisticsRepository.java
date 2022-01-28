package com.barden.bravo.statistics;

import com.barden.bravo.statistics.updater.StatisticsUpdater;
import com.barden.library.database.DatabaseRepository;
import com.barden.library.database.influx.InfluxProvider;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.TaskCreateRequest;
import com.influxdb.client.domain.TaskStatusType;
import com.influxdb.query.dsl.Flux;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Statistics repository class.
 */
public final class StatisticsRepository {

    /**
     * Initializes statistics repository object.
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
        DatabaseRepository.mongo().createIndex(
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
        InfluxProvider provider = DatabaseRepository.influx();
        InfluxDBClient client = provider.getClient();

        //Creates statistics bucket. (INFINITE)
        if (provider.findBucketByName("statistics").isEmpty())
            client.getBucketsApi().createBucket("statistics", provider.getOrganizationId());

        //Creates statistics bucket. (DAILY)
        if (provider.findBucketByName("statistics:daily").isEmpty())
            client.getBucketsApi().createBucket("statistics:daily",
                    new BucketRetentionRules().everySeconds((int) TimeUnit.DAYS.toSeconds(1)),
                    provider.getOrganizationId());

        //Creates flux string.
        String flux = """
                option task = {
                    name: "statistics_downsampling",
                    every: 1d,
                    offset: 0m
                }
                                
                """;

        //Adds remaining to the string.
        flux += Flux.from("statistics:daily")
                //Since a day.
                .range(-1L, ChronoUnit.DAYS)
                //Aggregate window.
                .aggregateWindow().withEvery("1d").withAggregateFunction("sum")
                //Write to infinite bucket.
                .to("statistics", "", """
                        ({"sum": r._value})
                         """)
                .toString();

        //If task is already created, no need to continue.
        if (provider.findTaskByName("statistics_downsampling").isPresent())
            return;

        //Creates task.
        client.getTasksApi().createTask(new TaskCreateRequest()
                .description("Downsampling statistics to daily statistics.")
                .status(TaskStatusType.ACTIVE)
                .orgID(provider.getOrganizationId())
                .flux(flux));
    }
}

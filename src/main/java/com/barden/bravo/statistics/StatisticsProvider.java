package com.barden.bravo.statistics;

import com.barden.bravo.statistics.type.StatisticType;
import com.barden.bravo.statistics.updater.StatisticsUpdater;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.database.influx.InfluxProvider;
import com.barden.library.scheduler.SchedulerProvider;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Statistics provider class.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class StatisticsProvider {

    public static final String INDEX = "statistics";
    public static final String INDEX_DAILY = "statistics:daily";
    public static final String INDEX_DOWNSAMPLING = "statistics:downsampling";

    /**
     * Initializes statistics provider.
     */
    public static void initialize() {
        //Handles databases.
        handleRedis();
        handleMongo();
        handleInflux();

        //Initializes statistics updater.
        StatisticsUpdater.initialize();

        //Scheduler provider.
        SchedulerProvider.create().every(15, TimeUnit.SECONDS).schedule(task -> {
            //Checks if we should do downsampling or not.
            boolean exists;
            try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
                exists = resource.exists(INDEX_DOWNSAMPLING);
            }

            //If it is exists, no need to downsampling yet.
            if (exists)
                return;

            //Declares required fields.
            HashMap<StatisticType, Double> statistics = new HashMap<>();
            HashMap<String, HashMap<StatisticType, Double>> players_statistics = new HashMap<>();
            List<Point> points = new ArrayList<>();

            //Queries game measurement.
            List<FluxTable> game_table = DatabaseProvider.influx().getClient().getQueryApi().query(
                    Flux.from(INDEX_DAILY)
                            .range(-1L, ChronoUnit.YEARS)
                            .filter(Restrictions.measurement().equal("game"))
                            .pivot(List.of("_time"), List.of("_field"), "_value")
                            .toString(),
                    DatabaseProvider.influx().getOrganizationId());
            //Queries player measurement.
            List<FluxTable> players_table = DatabaseProvider.influx().getClient().getQueryApi().query(
                    Flux.from(INDEX_DAILY)
                            .range(-1L, ChronoUnit.YEARS)
                            .filter(Restrictions.measurement().equal("players"))
                            .pivot(List.of("_time"), List.of("_field"), "_value")
                            .groupBy("user")
                            .toString(),
                    DatabaseProvider.influx().getOrganizationId());

            //Handles game table loop.
            for (FluxTable table : game_table) {
                for (var record : table.getRecords()) {
                    var values = record.getValues();

                    for (var statistic : StatisticType.values()) {
                        //Declares required fields.
                        var previous_value = statistics.getOrDefault(statistic, 0.0d);
                        var record_value = (Double) values.get(statistic.name());
                        record_value = record_value == null ? 0.0d : record_value;

                        //Calculates then saves to the cache map.
                        statistics.put(statistic, previous_value + record_value);
                    }
                }
            }

            //Handles player table loop.
            for (FluxTable table : players_table) {
                for (var record : table.getRecords()) {
                    var values = record.getValues();
                    var user_id = (String) values.get("player");
                    var player_statistics = players_statistics.getOrDefault(user_id, new HashMap<>());

                    for (var statistic : StatisticType.values()) {
                        //Declares required fields.
                        var previous_value = player_statistics.getOrDefault(statistic, 0.0d);
                        var record_value = (Double) values.get(statistic.name());
                        record_value = record_value == null ? 0.0d : record_value;

                        //Calculates then saves to the cache map.
                        player_statistics.put(statistic, previous_value + record_value);
                    }

                    players_statistics.put(user_id, player_statistics);
                }
            }

            //Converts all statistics to a point then add it to the points list.
            statistics.forEach((key, value) -> points.add(
                    Point.measurement("game")
                            .time(Instant.now(), WritePrecision.NS)
                            .addField(key.name(), value)));
            players_statistics.forEach((key, value) -> {
                Point point = Point.measurement("players")
                        .time(Instant.now(), WritePrecision.NS)
                        .addField("player", key);
                value.forEach((_key, _value) -> point.addField(_key.name(), _value));
                points.add(point);
            });

            //Writes all points to the no retention bucket. (INDEX) (DOWNSAMPLING END)
            DatabaseProvider.influx().getWriteAPIBlocking().writePoints(INDEX, DatabaseProvider.influx().getOrganizationId(), points);

            //Deletes game measurement.
            DatabaseProvider.influx().getClient().getDeleteApi().delete(
                    OffsetDateTime.now().minusYears(1),
                    OffsetDateTime.now().plusYears(1),
                    "",
                    INDEX_DAILY,
                    DatabaseProvider.influx().getOrganizationId()
            );

            //Resets downsampling date again.
            handleRedis();
        });
    }

    /**
     * Handles redis.
     */
    private static void handleRedis() {
        try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
            Pipeline pipeline = resource.pipelined();
            pipeline.set(INDEX_DOWNSAMPLING, LocalDate.now().toString());
            pipeline.expire(INDEX_DOWNSAMPLING, 60 * 60 * 24);
            pipeline.sync();

            if (!resource.save().equals("OK"))
                throw new IllegalStateException("Couldn't save statistics fields!");
        }
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
        if (provider.findBucketByName(INDEX).isEmpty())
            client.getBucketsApi().createBucket(INDEX, provider.getOrganizationId());

        //Creates statistics bucket. (DAILY)
        if (provider.findBucketByName(INDEX_DAILY).isEmpty())
            client.getBucketsApi().createBucket(INDEX_DAILY, provider.getOrganizationId());
    }
}

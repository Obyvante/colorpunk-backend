package com.barden.bravo.statistics.updater;

import com.barden.bravo.statistics.StatisticsProvider;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.scheduler.SchedulerProvider;
import com.google.gson.JsonObject;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Statistics updater class.
 */
public final class StatisticsUpdater {

    private static final Queue<JsonObject> queue = new LinkedList<>();
    private static boolean done = true;

    /**
     * Initializes statistics updater class.
     */
    public static void initialize() {
        SchedulerProvider.create().every(1, TimeUnit.SECONDS).schedule(task -> handle());
    }

    /**
     * Adds item to the queue.
     *
     * @param json_object Json object.
     */
    public static void addQueue(@Nonnull JsonObject json_object) {
        //Adds target item to the queue.
        queue.add(Objects.requireNonNull(json_object, "json object cannot be null!"));
    }

    /**
     * Handles next queue.
     */
    private static void handle() {
        //If queue is empty, no need to continue.
        if (queue.isEmpty())
            return;

        //If it is not done yet, no need to continue.
        if (!done)
            return;
        //Changes status.
        done = false;

        //Gets next element of queue.
        @Nonnull JsonObject json_object = queue.poll();

        //Handles exceptions.
        try {
            //Declares base fields.
            List<Point> points = new ArrayList<>();

            //Handles process.
            json_object.entrySet().forEach(entry -> {
                //Declares required fields.
                JsonObject content = entry.getValue().getAsJsonObject();

                //If the content is empty, no need to continue.
                if (content.isJsonNull() || content.size() == 0 || content.entrySet().size() == 0)
                    return;

                //Process players statistics.
                if (entry.getKey().equals("players")) {
                    //Loops through players.
                    content.entrySet().forEach((_entry) -> {
                        //Creates a point.
                        Point point = Point.measurement(entry.getKey())
                                .time(Instant.now(), WritePrecision.NS)
                                .addField("player", "id_" + _entry.getKey());

                        //Declares required fields.
                        @Nonnull JsonObject user_statistics_json = _entry.getValue().getAsJsonObject();

                        //Saves user statistics as a points.
                        user_statistics_json.entrySet().forEach((statistic_entry) ->
                                point.addField(statistic_entry.getKey(), statistic_entry.getValue().getAsDouble()));

                        //Adds configured point to the list.
                        points.add(point);
                    });
                } else if (entry.getKey().equals("game")) {
                    //Creates a point.
                    Point point = Point.measurement(entry.getKey()).time(Instant.now(), WritePrecision.NS);

                    //Handles game statistics.
                    content.entrySet().forEach((_entry) -> point.addField(_entry.getKey(), _entry.getValue().getAsDouble()));

                    //Adds configured point to the list.
                    points.add(point);
                } else {
                    //Creates a point.
                    Point point = Point.measurement(entry.getKey()).time(Instant.now(), WritePrecision.NS);

                    //Adds measurement fields.
                    entry.getValue().getAsJsonObject().entrySet().forEach((_entry) -> {
                        var _primitive = _entry.getValue().getAsJsonPrimitive();
                        if (_primitive.isNumber())
                            point.addField(_entry.getKey(), _primitive.getAsDouble());
                        else if (_primitive.isString())
                            point.addField(_entry.getKey(), _primitive.getAsString());
                    });

                    //Adds configured point to the list.
                    points.add(point);
                }
            });

            //If points list is empty, no need to fire save execution.
            if (points.isEmpty())
                return;

            //Writes point.
            DatabaseProvider.influx().getWriteAPI().writePoints(
                    StatisticsProvider.INDEX_DAILY,
                    DatabaseProvider.influx().getOrganizationId(),
                    points);
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't process queue item in statistics updater!", exception);
        } finally {
            done = true;
        }
    }

}

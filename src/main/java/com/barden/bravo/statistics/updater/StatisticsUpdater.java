package com.barden.bravo.statistics.updater;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseRepository;
import com.barden.library.scheduler.SchedulerRepository;
import com.google.gson.JsonObject;
import com.influxdb.client.write.Point;

import javax.annotation.Nonnull;
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
        SchedulerRepository.create().every(1, TimeUnit.SECONDS).schedule(task -> handle());
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

            //Gets json objects.
            @Nonnull com.google.gson.JsonObject players_json_object = json_object.getAsJsonObject("players");
            @Nonnull com.google.gson.JsonObject overall_json_object = json_object.getAsJsonObject("overall");

            //Handles players statistics.
            players_json_object.entrySet().forEach((entry) -> {
                //Declares base fields.
                Point point = new Point("players").addField("user", entry.getKey());

                //Declares required fields.
                @Nonnull JsonObject user_statistics_json_object = entry.getValue().getAsJsonObject();

                //Saves user statistics as a points.
                user_statistics_json_object.entrySet().forEach((statistic_entry) -> point.addField(statistic_entry.getKey(), statistic_entry.getValue().getAsDouble()));

                //Adds configured point to the list.
                points.add(point);
            });

            //Declares base fields.
            Point point = new Point("game");
            //Handles overall statistics.
            overall_json_object.entrySet().forEach((entry) -> point.addField(entry.getKey(), entry.getValue().getAsDouble()));
            //Adds configured point to the list.
            points.add(point);

            //Writes point.
            DatabaseRepository.influx().getWriteAPI().writePoints(points);
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't process queue item in statistics updater!", exception);
        } finally {
            //Marks as completed.
            done = true;
        }
    }

}

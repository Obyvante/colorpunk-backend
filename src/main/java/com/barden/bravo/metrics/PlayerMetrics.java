package com.barden.bravo.metrics;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * A class to handle player metric via timescaledb.
 */
public final class PlayerMetrics {

    /*
    VARIABLES
     */

    private static final String INSERT = "INSERT INTO player_metrics (time, player, type, value) VALUES (?, ?, ?, ?)";


    /*
    INITIALIZE
     */

    /**
     * Initializes player metrics.
     */
    public static void initialize() {
        createTables();
    }

    /**
     * Creates SQL tables.
     */
    private static void createTables() {
        try (var statement = DatabaseProvider.timescale().session()) {
            //Creates table for player metric.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS player_metrics (
                        time TIMESTAMPTZ NOT NULL,
                        player BIGINT NOT NULL,
                        type TEXT NOT NULL,
                        value DOUBLE PRECISION NOT NULL
                    )
                    """);
            //Creates hypertable.
            statement.execute("SELECT create_hypertable('player_metrics', 'time', if_not_exists => TRUE)");
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't create player metrics table!", exception);
        }
    }


    /*
    METHODS
     */

    /**
     * Writes player metrics to the timescaledb from json object.
     *
     * @param json Json object.
     */
    public static void write(@Nonnull JsonObject json) {
        try (var _insert = DatabaseProvider.timescale().prepare(INSERT)) {
            //Loops through players.
            json.entrySet().forEach((entry) -> {
                //Declares required fields.
                var metric_json = entry.getValue().getAsJsonObject();

                //Handles player metrics.
                metric_json.entrySet().forEach((metric_entry) -> {
                    try {
                        _insert.setTimestamp(1, Timestamp.from(Instant.now()));
                        _insert.setLong(2, Long.parseLong(entry.getKey()));
                        _insert.setString(3, metric_entry.getKey());
                        _insert.setDouble(4, metric_entry.getValue().getAsDouble());
                        _insert.addBatch();
                    } catch (Exception exception) {
                        BardenJavaLibrary.getLogger().error("Couldn't save players metrics to the database! [1]", exception);
                    }
                });
            });

            //Executes batch.
            _insert.executeBatch();
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't save players metrics to the database! [2]", exception);
        }
    }
}

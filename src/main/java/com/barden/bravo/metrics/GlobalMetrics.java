package com.barden.bravo.metrics;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * A class to handle global metric via timescaledb.
 */
public final class GlobalMetrics {

    /*
    VARIABLES
     */

    private static final String INSERT = "INSERT INTO global_metrics (time, type, value) VALUES (?, ?, ?)";


    /*
    INITIALIZE
     */

    /**
     * Initializes global metrics.
     */
    public static void initialize() {
        createTables();
    }

    /**
     * Creates SQL tables.
     */
    private static void createTables() {
        try (var statement = DatabaseProvider.timescale().session()) {
            //Creates table for global metric.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS global_metrics (
                        time TIMESTAMPTZ NOT NULL,
                        type TEXT NOT NULL,
                        value DOUBLE PRECISION NOT NULL
                    )
                    """);
            //Creates hypertable.
            statement.execute("SELECT create_hypertable('global_metrics', 'time', if_not_exists => TRUE)");
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't create global metrics table!", exception);
        }
    }


    /*
    METHODS
     */

    /**
     * Writes global metrics to the timescaledb from json object.
     *
     * @param json Json object.
     */
    public static void write(@Nonnull JsonObject json) {
        try (var _insert = DatabaseProvider.timescale().prepare(INSERT)) {
            json.entrySet().forEach((entry) -> {
                try {
                    _insert.setTimestamp(1, Timestamp.from(Instant.now()));
                    _insert.setString(2, entry.getKey());
                    _insert.setDouble(3, entry.getValue().getAsDouble());
                    _insert.addBatch();
                } catch (Exception exception) {
                    BardenJavaLibrary.getLogger().error("Couldn't save global metrics to the database! [1]", exception);
                }
            });

            //Executes batch.
            _insert.executeBatch();
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't save global metrics to the database! [2]", exception);
        }
    }
}

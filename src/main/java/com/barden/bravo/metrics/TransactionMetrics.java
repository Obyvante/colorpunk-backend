package com.barden.bravo.metrics;

import com.barden.bravo.transaction.Transaction;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * A class to handle transaction metrics via timescaledb.
 */
public final class TransactionMetrics {

    /*
    VARIABLES
     */

    private static final String INSERT = "INSERT INTO transaction_metrics (time, product, price, buyer) VALUES (?, ?, ?, ?)";


    /*
    INITIALIZE
     */

    /**
     * Initializes transaction metrics.
     */
    public static void initialize() {
        createTables();
    }

    /**
     * Creates SQL tables.
     */
    private static void createTables() {
        try (var statement = DatabaseProvider.timescale().session()) {
            //Creates table for transaction metric.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS transaction_metrics (
                        time TIMESTAMPTZ NOT NULL,
                        product BIGINT NOT NULL,
                        price INT NOT NULL,
                        buyer BIGINT NOT NULL
                    )
                    """);
            //Creates hypertable.
            statement.execute("SELECT create_hypertable('transaction_metrics', 'time', if_not_exists => TRUE)");
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't create transaction metrics table!", exception);
        }
    }


    /*
    METHODS
     */

    /**
     * Writes global metrics to the timescaledb.
     *
     * @param transaction Transaction.
     */
    public static void write(@Nonnull Transaction transaction) {
        try (var _insert = DatabaseProvider.timescale().prepare(INSERT)) {
            try {
                _insert.setTimestamp(1, Timestamp.from(Instant.now()));
                _insert.setLong(2, transaction.getProduct());
                _insert.setInt(3, transaction.getPrice());
                _insert.setLong(4, transaction.getBuyer());
                _insert.execute();
            } catch (Exception exception) {
                BardenJavaLibrary.getLogger().error("Couldn't save transaction metric to the database! [1]", exception);
            }
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't save transaction metric to the database! [2]", exception);
        }
    }
}

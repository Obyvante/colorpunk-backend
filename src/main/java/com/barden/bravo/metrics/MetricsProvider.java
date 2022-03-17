package com.barden.bravo.metrics;

import com.barden.library.database.DatabaseProvider;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

/**
 * Metrics provider class.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class MetricsProvider {

    /**
     * Initializes metrics provider.
     */
    public static void initialize() {
        //Handles databases.
        handleMongo();
        handleTimescale();

        //Initializes metrics.
        GlobalMetrics.initialize();
        PlayerMetrics.initialize();
        TransactionMetrics.initialize();
    }

    /**
     * Handles mongo.
     */
    private static void handleMongo() {
        //Unique indexes.
        DatabaseProvider.mongo().createIndex(
                "bravo",
                "metrics",
                Indexes.ascending("type"),
                new IndexOptions().unique(true).background(true));
    }

    /**
     * Handles mongo.
     */
    private static void handleTimescale() {
    }
}

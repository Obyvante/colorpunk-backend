package com.barden.bravo.transaction.provider;

import com.barden.library.database.DatabaseProvider;
import com.barden.library.database.influx.InfluxProvider;
import com.influxdb.client.InfluxDBClient;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

/**
 * Transaction provider.
 */
public final class TransactionProvider {

    /**
     * Handles mongo.
     */
    private static void handleMongo() {
        //Unique indexes.
        DatabaseProvider.mongo().createIndex(
                "bravo",
                "transactions",
                Indexes.ascending("uid"),
                new IndexOptions().unique(true).background(true));
        //Not unique indexes.
        DatabaseProvider.mongo().createIndex(
                "bravo",
                "transactions",
                Indexes.ascending("date", "buyer", "product"),
                new IndexOptions().background(true));
    }

    /**
     * Handles influx.
     */
    private static void handleInflux() {
        //Declares required fields.
        InfluxProvider provider = DatabaseProvider.influx();
        InfluxDBClient client = provider.getClient();

        //Creates statistics bucket. (INFINITE)
        if (provider.findBucketByName(TransactionStatisticsProvider.BUCKET).isEmpty())
            client.getBucketsApi().createBucket(TransactionStatisticsProvider.BUCKET, provider.getOrganizationId());
    }

    /**
     * Initializes transaction provider.
     */
    public static void initialize() {
        handleMongo();
        handleInflux();
    }
}

package com.barden.bravo.transaction.provider;

import com.barden.bravo.transaction.Transaction;
import com.barden.library.database.DatabaseProvider;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Transaction statistics provider to write statistics to influx.
 */
public final class TransactionStatisticsProvider {

    public static final String BUCKET = "transactions";

    /**
     * Writes transaction statistic.
     *
     * @param transaction Transaction.
     */
    public static void write(@Nonnull Transaction transaction) {
        Objects.requireNonNull(transaction);

        var point = Point.measurement(BUCKET)
                .time(Instant.now(), WritePrecision.NS)
                .addField("product", transaction.getProduct())
                .addField("buyer", transaction.getBuyer())
                .addField("price", transaction.getPrice());

        DatabaseProvider.influx().getWriteAPI().writePoint(
                BUCKET,
                DatabaseProvider.influx().getOrganizationId(),
                point);
    }

    /**
     * Writes transaction statistic.
     *
     * @param transactions Transactions.
     */
    public static void write(@Nonnull HashSet<Transaction> transactions) {
        assert !Objects.requireNonNull(transactions).isEmpty() : "transactions list cannot be empty!";

        List<Point> points = new ArrayList<>();

        transactions.forEach(transaction -> points.add(Point.measurement(BUCKET)
                .time(Instant.now(), WritePrecision.NS)
                .addField("product", transaction.getProduct())
                .addField("buyer", transaction.getBuyer())
                .addField("price", transaction.getPrice())));

        DatabaseProvider.influx().getWriteAPI().writePoints(
                BUCKET,
                DatabaseProvider.influx().getOrganizationId(),
                points);
    }
}

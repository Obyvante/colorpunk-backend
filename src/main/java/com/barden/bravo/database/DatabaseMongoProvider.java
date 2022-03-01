package com.barden.bravo.database;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.scheduler.SchedulerProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Database mongo provider class to provider database methods.
 */
public abstract class DatabaseMongoProvider {

    private final String databaseId;
    private final String collectionId;

    /**
     * Creates a database mongo provider.
     *
     * @param databaseId   Database id.
     * @param collectionId Collection id.
     */
    public DatabaseMongoProvider(@Nonnull String databaseId, @Nonnull String collectionId) {
        this.databaseId = Objects.requireNonNull(databaseId);
        this.collectionId = Objects.requireNonNull(collectionId);
    }

    /**
     * Gets database id.
     *
     * @return Database id.
     */
    @Nonnull
    public String getDatabaseId() {
        return this.databaseId;
    }

    /**
     * Gets collection id.
     *
     * @return Collection id.
     */
    @Nonnull
    public String getCollectionId() {
        return this.collectionId;
    }

    /**
     * Adds mongo index.
     *
     * @param index   Bson index.
     * @param options Index options.
     */
    public final void addIndex(@Nonnull Bson index, @Nonnull IndexOptions options) {
        DatabaseProvider.mongo().createIndex(this.databaseId, this.collectionId, index, options);
    }

    /**
     * Gets mongo collection.
     *
     * @return Mongo collection.
     */
    @Nonnull
    public final MongoCollection<BsonDocument> getCollection() {
        return Objects.requireNonNull(DatabaseProvider.mongo().getCollection(this.databaseId, this.collectionId, BsonDocument.class), "database(" + this.databaseId + ") collection cannot be null!");
    }

    /**
     * Saves database objects to the database.
     *
     * @param objects Database objects.
     */
    public final void save(@Nonnull Set<? extends DatabaseObject<?, ?>> objects) {
        //Object null checks.
        Objects.requireNonNull(objects, "Tried to save null database(" + this.databaseId + ") structure list to the database.");
        if (objects.size() == 0)
            return;

        try {
            MongoCollection<BsonDocument> collection = this.getCollection();

            //If there is only one database structure, no need to use bulk since it impacts performance.
            if (objects.size() == 1) {
                objects.iterator().next().getDatabase().save();
                return;
            }

            List<WriteModel<BsonDocument>> writes = new ArrayList<>();
            //Loops through database objects, converts "save module" then adds to the created write models list.
            objects.forEach(structure -> writes.add(new UpdateOneModel<>(structure.getDatabase().toQueryBson(), structure.getDatabase().toSaveBson())));

            //Pass write modules to collection. (UPDATES MONGO BSON DOCUMENTS AND COLLECTION) -> NOT ASYNC!
            collection.bulkWrite(writes, new BulkWriteOptions().bypassDocumentValidation(true));
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't save database(" + this.databaseId + ") objects to the database!", exception);
        }
    }

    /**
     * Saves database objects to the database. (ASYNC)
     *
     * @param objects Database objects.
     */
    public final void saveAsync(@Nonnull Set<? extends DatabaseObject<?, ?>> objects) {
        SchedulerProvider.schedule(_task -> this.save(objects));
    }
}

package com.barden.bravo.database;

import com.barden.library.scheduler.SchedulerProvider;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Database structure class to handle database methods.
 *
 * @param <P> Parent object.
 * @param <F> Database field enum.
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class DatabaseStructure<P, F extends DatabaseField<P>> {

    protected final P parent;
    protected final Class<F> field;
    protected final DatabaseMongoProvider provider;

    /**
     * Creates a database structure with given parent and field.
     *
     * @param parent Parent.
     * @param field  Field enum.
     */
    public DatabaseStructure(@Nonnull P parent, @Nonnull Class<F> field, @Nonnull DatabaseMongoProvider provider) {
        this.parent = Objects.requireNonNull(parent);
        this.field = Objects.requireNonNull(field);
        this.provider = Objects.requireNonNull(provider);
    }

    /**
     * Gets parent.
     *
     * @return Parent.
     */
    @Nonnull
    public final P getParent() {
        return this.parent;
    }

    /**
     * Gets field class.
     *
     * @return Field class.
     */
    @Nonnull
    public final Class<F> getField() {
        return this.field;
    }

    /**
     * Gets mongo provider.
     *
     * @return Mongo provider.
     */
    @Nonnull
    public final DatabaseMongoProvider getMongoProvider() {
        return this.provider;
    }


    /*
    CALLS
     */

    /**
     * Saves database structure to the database.
     *
     * @param fields Fields to save.
     */
    public final void save(@Nonnull F... fields) {
        //Object null checks.
        Objects.requireNonNull(fields, "Tried to save database(" + this.provider.getDatabaseId() + ") structure(" + this.parent + ") without fields.");
        this.provider.getCollection().updateOne(this.toQueryBson(), this.toSaveBson(fields), new UpdateOptions().upsert(true));
    }

    /**
     * Saves database structure to the database.
     *
     * @param fields Fields to save.
     */
    public final void save(@Nonnull Collection<F> fields) {
        //Object null checks.
        Objects.requireNonNull(fields, "Tried to save database(" + this.provider.getDatabaseId() + ") structure(" + this.parent + ") without fields.");
        this.save((F[]) fields.toArray(DatabaseField[]::new));
    }

    /**
     * Saves database structure to the database with all fields.
     */
    public final void save() {
        this.save(this.field.getEnumConstants());
    }

    /**
     * Saves database structure to the database async.
     *
     * @param fields Fields to save.
     */
    public final void saveAsync(@Nonnull F... fields) {
        SchedulerProvider.schedule(_task -> this.save(fields));
    }

    /**
     * Saves database structure to the database. (ASYNC)
     *
     * @param fields Fields to save.
     */
    public final void saveAsync(@Nonnull Collection<F> fields) {
        //Object null checks.
        Objects.requireNonNull(fields, "Tried to save database(" + this.provider.getDatabaseId() + ") structure(" + this.parent + ") to database without fields.");
        this.saveAsync((F[]) fields.toArray(DatabaseField[]::new));
    }

    /**
     * Saves database structure to the database with all fields.  (ASYNC)
     */
    public final void saveAsync() {
        this.saveAsync(this.field.getEnumConstants());
    }

    /**
     * Deletes database structure from the database.
     */
    public final void delete() {
        this.provider.getCollection().deleteOne(this.toQueryBson());
    }

    /**
     * Deletes database structure from the database. (ASYNC)
     */
    public final void deleteAsync() {
        SchedulerProvider.schedule(_task -> this.delete());
    }


    /*
    CONVERTERS
     */

    /**
     * Gets query field.
     *
     * @return Bson.
     */
    @Nonnull
    public final Bson toQueryBson() {
        for (F field : this.field.getEnumConstants())
            if (field.isQuery()) return new BsonDocument(field.getPath(), field.toBsonValue(this.parent));
        return new BsonDocument();
    }

    /**
     * Gets parent as a save bson.
     * With save bson, we can update mongo document.
     *
     * @return Bson.
     */
    @Nonnull
    public final Bson toSaveBson() {
        return this.toSaveBson(this.field.getEnumConstants());
    }

    /**
     * Gets save bson for target fields.
     *
     * @param fields Fields to convert bson.
     * @return Bson.
     */
    @SafeVarargs
    @Nonnull
    public final Bson toSaveBson(@Nonnull F... fields) {
        //Object null checks.
        Objects.requireNonNull(fields, "Database(" + this.provider.getDatabaseId() + ") structure(" + this.parent + ") fields cannot be null!");
        List<Bson> list = new ArrayList<>();
        for (F field : fields) list.add(Updates.set(field.getPath(), this.toBsonValue(field)));
        return Updates.combine(list);
    }

    /**
     * Gets bson value from field.
     *
     * @param field Field to get value.
     * @return Bson value.
     */
    @Nonnull
    public final BsonValue toBsonValue(@Nonnull F field) {
        return Objects.requireNonNull(field, "Database(" + this.provider.getDatabaseId() + ") structure(" + this.parent + ") field cannot be null!").toBsonValue(this.parent);
    }
}

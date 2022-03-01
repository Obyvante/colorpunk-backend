package com.barden.bravo.database;

import org.bson.BsonValue;

import javax.annotation.Nonnull;

/**
 * Database field interface to handle required methods.
 */
public interface DatabaseField<P> {

    /**
     * Gets field path.
     *
     * @return Field path.
     */
    @Nonnull
    String getPath();

    /**
     * Checks if enum field is query field.
     *
     * @return if enum field is query field, returns true.
     */
    boolean isQuery();

    /**
     * Gets bson value of current field.
     *
     * @param parent Parent.
     * @return Bson value.
     */
    @Nonnull
    BsonValue toBsonValue(@Nonnull P parent);
}
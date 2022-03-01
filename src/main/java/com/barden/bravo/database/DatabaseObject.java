package com.barden.bravo.database;

import javax.annotation.Nonnull;

/**
 * Database object.
 *
 * @param <P> Parent.
 * @param <F> Database field.
 */
public interface DatabaseObject<P, F extends DatabaseField<P>> {

    /**
     * Gets database structure.
     *
     * @return Database structure
     */
    @Nonnull
    DatabaseStructure<P, F> getDatabase();
}

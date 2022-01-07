package com.barden.bravo.trail;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Trail abstract class.
 */
public abstract class Trail {

    protected final int id;
    private final String name;
    private final String assetId;

    /**
     * Creates trail object.
     *
     * @param id      Trail id.
     * @param name    Trail name.
     * @param assetId Trail asset id. (PACKAGE)
     */
    public Trail(int id, @Nonnull String name, @Nonnull String assetId) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.assetId = Objects.requireNonNull(assetId, "assetId cannot be null!");
    }


    /**
     * Gets trail id.
     *
     * @return Trail id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets name.
     *
     * @return Trail name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Gets asset id.
     *
     * @return Trail asset id. (PACKAGE)
     */
    @Nonnull
    public String getAssetId() {
        return this.assetId;
    }
}

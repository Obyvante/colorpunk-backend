package com.barden.bravo.trail.parts;

import com.barden.bravo.trail.Trail;
import org.jetbrains.annotations.NotNull;

/**
 * Feet trail class.
 */
public final class FeetTrail extends Trail {
    /**
     * Creates feet trail object.
     *
     * @param id      Trail id.
     * @param name    Trail name.
     * @param assetId Trail asset id. (PACKAGE)
     */
    public FeetTrail(int id, @NotNull String name, @NotNull String assetId) {
        super(id, name, assetId);
    }
}

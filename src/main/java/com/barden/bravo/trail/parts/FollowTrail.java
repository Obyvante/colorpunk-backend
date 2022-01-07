package com.barden.bravo.trail.parts;

import com.barden.bravo.trail.Trail;
import org.jetbrains.annotations.NotNull;

/**
 * Follow trail class.
 */
public final class FollowTrail extends Trail {
    /**
     * Creates follow trail object.
     *
     * @param id      Trail id.
     * @param name    Trail name.
     * @param assetId Trail asset id. (PACKAGE)
     */
    public FollowTrail(int id, @NotNull String name, @NotNull String assetId) {
        super(id, name, assetId);
    }
}

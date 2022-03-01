package com.barden.bravo.player;

import com.barden.bravo.database.DatabaseMongoProvider;

/**
 * Player mongo provider class.
 */
public final class PlayerMongoProvider extends DatabaseMongoProvider {

    /**
     * Creates a player mongo provider.
     */
    public PlayerMongoProvider() {
        super("bravo", "players");
    }
}

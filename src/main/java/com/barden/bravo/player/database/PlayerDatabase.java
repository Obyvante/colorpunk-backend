package com.barden.bravo.player.database;

import com.barden.bravo.database.DatabaseStructure;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerProvider;

import javax.annotation.Nonnull;

/**
 * Player database class.
 * This class is mostly for database calls and gets. It also has useful
 * database methods/functions to make code shorter and safer.
 */
public final class PlayerDatabase extends DatabaseStructure<Player, PlayerBsonField> {

    /**
     * Creates a player database.
     *
     * @param player Player.
     */
    public PlayerDatabase(@Nonnull Player player) {
        super(player, PlayerBsonField.class, PlayerProvider.getMongoProvider());
    }
}

package com.barden.bravo.player;

import com.barden.bravo.player.database.PlayerBsonField;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.scheduler.SchedulerProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.BsonDocument;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Player mongo provider class.
 */
public final class PlayerMongoProvider {

    private static final String DATABASE = "bravo";
    private static final String COLLECTION = "players";

    /**
     * Gets player mongo collection.
     *
     * @return Player mongo collection.
     */
    @Nonnull
    public static MongoCollection<BsonDocument> getCollection() {
        return Objects.requireNonNull(DatabaseProvider.mongo().getCollection(DATABASE, COLLECTION, BsonDocument.class), "player mongo collection cannot be null!");
    }

    /**
     * Updates players mongo document.
     *
     * @param players Players.
     * @param async   Should async or not.
     */
    public static void update(@Nonnull Set<Player> players, boolean async) {
        //Handles async.
        if (async)
            SchedulerProvider.schedule(task -> PlayerMongoProvider.update(players));
        else
            PlayerMongoProvider.update(players);
    }

    /**
     * Updates players bson document. (MONGO) (ASYNC)
     *
     * @param players Players.
     */
    public static void updateAsync(@Nonnull Player... players) {
        PlayerMongoProvider.update(Arrays.stream(players).collect(Collectors.toSet()), true);
    }

    /**
     * Updates players bson document. (MONGO) (SYNC)
     *
     * @param players Players.
     */
    public static void update(@Nonnull Player... players) {
        PlayerMongoProvider.update(Arrays.stream(players).collect(Collectors.toSet()), true);
    }

    /**
     * Updates players bson document. (MONGO) (SYNC)
     *
     * @param players Players.
     */
    public static void update(@Nonnull Set<Player> players) {
        //Objects null check.
        Objects.requireNonNull(players, "players cannot be null!");

        //If player is empty, no need to continue.
        if (players.isEmpty())
            return;

        try {
            //Gets player mongo collection.
            MongoCollection<BsonDocument> collection = PlayerMongoProvider.getCollection();

            //If there is only one player, no need to continue.
            if (players.size() == 1) {
                //Updates player.
                players.forEach(player -> player.getDatabase().update(PlayerBsonField.values()));
                return;
            }

            //Creates player write models list to handle bulk write/update.
            List<WriteModel<BsonDocument>> player_write_models = new ArrayList<>();
            //Loops through players, converts "update module" then adds to the created write models list.
            players.forEach(player -> player_write_models.add(new UpdateOneModel<>(
                    player.getDatabase().toQueryBson(),
                    player.getDatabase().toUpdateBson(PlayerBsonField.values()))));

            //Pass write modules to collection. (UPDATES MONGO BSON DOCUMENTS AND COLLECTION) -> NOT ASYNC!
            collection.bulkWrite(player_write_models, new BulkWriteOptions().bypassDocumentValidation(true));
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't update players!", exception);
        }
    }
}

package com.barden.bravo.player;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseRepository;
import com.barden.library.scheduler.SchedulerRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Player mongo provider class.
 */
public final class PlayerMongoProvider {

    private static final String DATABASE = "bravo";
    private static final String COLLECTION = "players";

    /*
    DATABASE METHODS
     */

    /**
     * Gets player mongo collection.
     *
     * @return Mongo collection. (BSON DOCUMENT)
     */
    @Nonnull
    public static MongoCollection<BsonDocument> getCollection() {
        return Objects.requireNonNull(DatabaseRepository.mongo().getCollection(DATABASE, COLLECTION, BsonDocument.class), "player mongo collection cannot be null!");
    }


    /*
    PLAYER METHODS
     */

    /**
     * Updates players mongo document.
     *
     * @param players Players.
     * @param async   Should async or not.
     */
    public static void update(@Nonnull Collection<Player> players, boolean async) {
        //Handles async.
        if (async)
            SchedulerRepository.schedule(task -> update(players));
        else
            PlayerMongoProvider.update(players);
    }

    /**
     * Updates players bson document. (MONGO) (ASYNC)
     *
     * @param players Players.
     */
    public static void updateAsync(@Nonnull Player... players) {
        PlayerMongoProvider.update(Arrays.stream(players).collect(Collectors.toList()), true);
    }

    /**
     * Updates players bson document. (MONGO) (SYNC)
     *
     * @param players Players.
     */
    public static void update(@Nonnull Player... players) {
        PlayerMongoProvider.update(Arrays.stream(players).collect(Collectors.toList()), true);
    }

    /**
     * Updates players bson document. (MONGO) (SYNC)
     *
     * @param players Players.
     */
    public static void update(@Nonnull Collection<Player> players) {
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
                players.forEach(player -> PlayerMongoProvider.getCollection().updateOne(player.toQueryBson(), player.toUpdateBson(), new UpdateOptions()));
                return;
            }

            //Creates player write models list to handle bulk write/update.
            List<WriteModel<BsonDocument>> player_write_models = new ArrayList<>();
            //Loops through players, converts "update module" then adds to the created write models list.
            players.forEach(player -> player_write_models.add(new UpdateOneModel<>(player.toQueryBson(), player.toUpdateBson())));

            //Pass write modules to collection. (UPDATES MONGO BSON DOCUMENTS AND COLLECTION) -> NOT ASYNC!
            collection.bulkWrite(player_write_models, new BulkWriteOptions().bypassDocumentValidation(true));
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't update players!", exception);
        }
    }

    /**
     * Saves player to the database.
     *
     * @param player Player.
     * @param async  Should async or not.
     */
    public static void save(@Nonnull Player player, boolean async) {
        //Handles async.
        if (async)
            SchedulerRepository.schedule(task -> save(player));
        else
            PlayerMongoProvider.save(player);
    }

    /**
     * Saves player to the database.
     *
     * @param player Player.
     */
    public static void save(@Nonnull Player player) {
        //Objects null check.
        Objects.requireNonNull(player, "player cannot be null!");

        //Gets mongo collection.
        MongoCollection<BsonDocument> collection = PlayerMongoProvider.getCollection();

        //If player is already created, no need to continue.
        if (collection.find(player.toQueryBson()).limit(1).cursor().hasNext())
            return;

        //Saves/updates to the database.
        collection.updateOne(player.toQueryBson(), new Document("$set", player.toBsonDocument()), new UpdateOptions().upsert(true));
    }

}

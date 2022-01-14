package com.barden.bravo.player;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseRepository;
import com.barden.library.scheduler.SchedulerRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Player repository class.
 */
public final class PlayerRepository {

    /**
     * Initializes player repository object.
     */
    public static void initialize() {
        //Creates mongo indexes.
        createMongoIndexes();

        //Pushes players updated data to mongo.
        SchedulerRepository.create().after(5, TimeUnit.MINUTES).every(1, TimeUnit.MINUTES).schedule(task -> PlayerMongoProvider.update(content));

        //Logging.
        BardenJavaLibrary.getLogger().info("Player repository is initialized successfully!");
    }

    /**
     * Creates mongo indexes for players collection. (Performance and querying)
     */
    private static void createMongoIndexes() {
        //Unique indexes.
        DatabaseRepository.mongo().createIndex(
                "bravo",
                "players",
                Indexes.ascending("id"), new IndexOptions().unique(true).background(true));
    }


    /*
    ROOT
     */

    private static final HashSet<Player> content = new HashSet<>();

    /**
     * Gets players.
     *
     * @return Players.
     */
    @Nonnull
    public static HashSet<Player> getContent() {
        return content;
    }

    /**
     * Finds player. (SAFE)
     *
     * @param id Roblox user id.
     * @return Optional player.
     */
    @Nonnull
    public static Optional<Player> find(long id) {
        return content.stream().filter(player -> player.getId() == id).findFirst();
    }

    /**
     * Gets player. (UNSAFE)
     *
     * @param id Roblox user id.
     * @return Player.
     */
    @Nonnull
    public static Player get(long id) {
        return find(id).orElseThrow(() -> new NullPointerException("player cannot be null!"));
    }

    /**
     * Removes player object if it is existed in cache.
     *
     * @param id Roblox user id.
     */
    public static void remove(long id) {
        PlayerRepository.find(id).ifPresent(content::remove);
    }

    /**
     * Handles player on both cache and database.
     * <p>
     * HOW IT WORKS?
     * <p>
     * If player is already exist in cache, it returns it.
     * If not, it will try to find player in database, if
     * it finds, it will return. If not, it will create new
     * player object then save it to the database and cache
     * then returns it.
     *
     * @param id     Roblox user id.
     * @param insert Should insert new player to the database if it is not exist.
     * @return Created or existed player.
     */
    @Nonnull
    public static Player handle(long id, boolean insert) {
        //Gets player from the cache.
        Player player = PlayerRepository.find(id).orElse(null);
        //If it is already exist in cache, no need to continue.
        if (player != null)
            return player;

        //Gets mongo collection.
        MongoCollection<BsonDocument> collection = PlayerMongoProvider.getCollection();
        //Declares required fields.
        Document id_bson = new Document("id", id);
        MongoCursor<BsonDocument> player_document_cursor = collection.find(id_bson).limit(1).cursor(); // NOT ASYNC! -> IT WILL FREEZE MAIN THREAD.
        //If player is already exist in database, no need to continue.
        if (player_document_cursor.hasNext()) {
            //Creates player object from document. (DOCUMENT -> MONGO BSON)
            player = new Player(id, player_document_cursor.next());
            //Adds created player object to the cache.
            content.add(player);
            //Returns created player object.
            return player;
        }

        //If it shouldn't insert to database, throws exception.
        if (!insert)
            throw new IllegalStateException("player cannot be created due to database insertion.");

        //Creates player object.
        player = new Player(id);
        //Saves to the database.
        PlayerMongoProvider.save(player, false);

        //Adds created player object to the cache.
        content.add(player);

        //Returns created player object.
        return player;
    }
}

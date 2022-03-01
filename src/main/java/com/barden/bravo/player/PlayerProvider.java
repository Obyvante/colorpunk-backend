package com.barden.bravo.player;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.scheduler.SchedulerProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Player provider class.
 */
public final class PlayerProvider {

    /*
    STATICS
     */

    private static final PlayerMongoProvider mongoProvider = new PlayerMongoProvider();

    /**
     * Gets player mongo provider.
     *
     * @return Player mongo provider.
     */
    @Nonnull
    public static PlayerMongoProvider getMongoProvider() {
        return mongoProvider;
    }

    /**
     * Initializes player provider.
     */
    public static void initialize() {
        PlayerProvider.getMongoProvider().addIndex(Indexes.ascending("id"), new IndexOptions().unique(true).background(true));

        //Pushes players updated data to mongo.
        SchedulerProvider.create()
                .after(5, TimeUnit.MINUTES)
                .every(1, TimeUnit.MINUTES)
                .schedule(task -> PlayerProvider.getMongoProvider().save(content.values()));

        //Logging.
        BardenJavaLibrary.getLogger().info("Player provider is initialized successfully!");
    }


    /*
    BODY
     */

    private static final BiMap<Long, Player> content = HashBiMap.create();

    /**
     * Gets players.
     *
     * @return Players.
     */
    @Nonnull
    public static Set<Player> getContent() {
        return content.values();
    }

    /**
     * Finds player by its roblox user id. (SAFE)
     *
     * @param id Player roblox user id.
     * @return Optional player.
     */
    @Nonnull
    public static Optional<Player> find(long id) {
        return Optional.ofNullable(content.get(id));
    }

    /**
     * Gets player by its roblox user id. (UNSAFE)
     *
     * @param id Player roblox user id.
     * @return Player.
     */
    @Nonnull
    public static Player get(long id) {
        return find(id).orElseThrow(() -> new NullPointerException("player cannot be null!"));
    }

    /**
     * Removes player.
     *
     * @param id Player roblox user id.
     */
    public static void remove(long id) {
        content.remove(id);
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
     * @param name   Roblox name.
     * @param insert Should insert new player to the database if it is not exist.
     * @return Created or existed player.
     */
    @Nonnull
    public static Player handle(long id, @Nonnull String name, boolean insert) {
        //Gets player from the cache.
        Player player = PlayerProvider.find(id).orElse(null);
        //If it is already exist in cache, no need to continue.
        if (player != null)
            return player;

        //Gets mongo collection.
        MongoCollection<BsonDocument> collection = PlayerProvider.getMongoProvider().getCollection();
        //Declares required fields.
        Document id_bson = new Document("id", id);
        MongoCursor<BsonDocument> player_document_cursor = collection.find(id_bson).limit(1).cursor(); // NOT ASYNC! -> IT WILL FREEZE MAIN THREAD.
        //If player is already exist in database, no need to continue.
        if (player_document_cursor.hasNext()) {
            //Creates player object from document. (DOCUMENT -> MONGO BSON)
            player = new Player(id, player_document_cursor.next());
            //Adds created player object to the cache.
            content.put(player.getId(), player);
            //Returns created player object.
            return player;
        }

        //If it shouldn't insert to database, throws exception.
        if (!insert)
            throw new IllegalStateException("player cannot be created due to database insertion.");

        //Creates player object.
        player = new Player(id, name);
        //Saves to the database.
        player.getDatabase().save();

        //Adds created player object to the cache.
        content.put(player.getId(), player);

        //Returns created player object.
        return player;
    }
}

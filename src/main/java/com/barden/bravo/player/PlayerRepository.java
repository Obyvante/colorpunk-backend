package com.barden.bravo.player;

import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

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
     * @param id Roblox user id.
     * @return Created or existed player.
     */
    @Nonnull
    public static Player handle(long id) {
        //Gets player from the cache.
        Player player = PlayerRepository.find(id).orElse(null);
        //If it is already exist in cache, no need to continue.
        if (player != null)
            return player;

        //Gets mongo collection.
        MongoCollection<Document> collection = Objects.requireNonNull(DatabaseRepository.mongo().getCollection("bravo", "players"), "players collection cannot be null!");
        //Declares required fields.
        Document id_bson = new Document("id", id);
        MongoCursor<Document> player_document_cursor = collection.find(id_bson).limit(1).cursor(); // NOT ASYNC! -> IT WILL FREEZE MAIN THREAD.
        //If player is already exist in database, no need to continue.
        if (player_document_cursor.hasNext()) {
            //Creates player object from document. (DOCUMENT -> MONGO BSON)
            player = new Player(id, player_document_cursor.next());
            //Adds created player object to the cache.
            content.add(player);
            //Returns created player object.
            return player;
        }

        //Creates player object.
        player = new Player(id);
        //Saves to the database.
        player.save();

        //Adds created player object to the cache.
        content.add(player);

        //Returns created player object.
        return player;
    }
}

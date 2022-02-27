package com.barden.bravo.player.database;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerMongoProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * Player database class.
 * This class is mostly for database calls and gets. It also has useful
 * database methods/functions to make code shorter and safer.
 */
public final class PlayerDatabase {

    private final Player player;

    /**
     * Creates a player database.
     *
     * @param player Player.
     */
    public PlayerDatabase(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player);
    }

    /**
     * Gets player.
     *
     * @return Player.
     */
    @Nonnull
    public Player getPlayer() {
        return this.player;
    }


    /*
    CALLS
     */

    /**
     * Saves player to the database.
     */
    public void save() {
        MongoCollection<BsonDocument> collection = PlayerMongoProvider.getCollection();

        //If player is already created, no need to continue.
        if (collection.find(this.toQueryBson()).limit(1).cursor().hasNext())
            return;

        //Saves/updates to the database.
        collection.insertOne(this.toBsonDocument());
    }

    /**
     * Updates player database.
     */
    public void update(@Nonnull PlayerBsonField... fields) {
        PlayerMongoProvider.getCollection().updateOne(this.toQueryBson(), this.toUpdateBson(Objects.requireNonNull(fields)));
    }



    /*
    CONVERTERS
     */

    /**
     * Gets player's query field.
     * It'll be useful to 'query/filter' player
     * with unique identifier.
     *
     * @return Query bson.
     */
    @Nonnull
    public Bson toQueryBson() {
        return new BsonDocument(PlayerBsonField.ID.getPath(), new BsonInt64(this.player.getId()));
    }

    /**
     * Gets player as a bson document.
     *
     * @return Player bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument bson_document = new BsonDocument();
        for (PlayerBsonField field : PlayerBsonField.values())
            bson_document.put(field.getPath(), this.toBsonValue(field));
        return bson_document;
    }

    /**
     * Gets player as an update bson.
     * With update bson, you can update mongo player collection.
     *
     * @return Player update bson.
     */
    @Nonnull
    public Bson toUpdateBson(@Nonnull PlayerBsonField... fields) {
        return Updates.combine(Arrays.stream(Objects.requireNonNull(fields))
                .map(field -> new BsonDocument(field.getPath(), this.toBsonValue(field)))
                .toList());
    }

    /**
     * Gets player bson value by field.
     *
     * @param field Player bson field.
     * @return Bson value.
     */
    @Nonnull
    public BsonValue toBsonValue(@Nonnull PlayerBsonField field) {
        return switch (Objects.requireNonNull(field)) {
            case ID -> new BsonInt64(this.player.getId());
            case NAME -> new BsonString(this.player.getName());
            case INVENTORY -> this.player.getInventory().toBsonDocument();
            case CURRENCIES -> this.player.getCurrencies().toBsonDocument();
            case STATS -> this.player.getStats().toBsonDocument();
            case SETTINGS -> this.player.getSettings().toBsonDocument();
            case STATISTICS -> this.player.getStatistics().toBsonDocument();
        };
    }
}

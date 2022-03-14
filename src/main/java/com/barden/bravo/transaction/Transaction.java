package com.barden.bravo.transaction;

import com.barden.library.database.DatabaseProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Transaction.
 */
@SuppressWarnings({"unused", "null"})
public final class Transaction {

    private final UUID uid;
    private final Date date;
    private final long product;
    private final long buyer;
    private final int price;
    private transient boolean saved;

    /**
     * Creates a transaction.
     *
     * @param uid     Unique id.
     * @param date    Date.
     * @param product Product. (ROBLOX GAME PASS/PRODUCT ID)
     * @param buyer   Buyer. (ROBLOX USER ID)
     * @param price   Price. (ROBUX)
     */
    public Transaction(UUID uid, Date date, long product, long buyer, int price) {
        this.uid = Objects.requireNonNull(uid);
        this.date = Objects.requireNonNull(date);
        this.product = product;
        this.buyer = buyer;
        this.price = price;
    }

    /**
     * Creates a transaction.
     *
     * @param product Product. (ROBLOX GAME PASS/PRODUCT ID)
     * @param buyer   Buyer. (ROBLOX USER ID)
     * @param price   Price. (ROBUX)
     */
    public Transaction(long product, long buyer, int price) {
        this(UUID.randomUUID(), new Date(), product, buyer, price);
    }

    /**
     * Gets transaction unique id.
     *
     * @return Transaction unique id.
     */
    @Nonnull
    public UUID getUid() {
        return this.uid;
    }

    /**
     * Gets transaction date.
     *
     * @return Transaction date.
     */
    @Nonnull
    public Date getDate() {
        return this.date;
    }

    /**
     * Gets transaction product.
     *
     * @return Transaction product. (ROBLOX GAME PASS/PRODUCT ID)
     */
    public long getProduct() {
        return this.product;
    }

    /**
     * Gets transaction buyer.
     *
     * @return Transaction buyer. (ROBLOX USER ID)
     */
    public long getBuyer() {
        return this.buyer;
    }

    /**
     * Gets transaction price.
     *
     * @return Transaction price. (ROBUX)
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Gets if transaction is saved to database or not.
     *
     * @return If transaction is saved to database or not.
     */
    public boolean isSaved() {
        return this.saved;
    }

    /**
     * Sets save status of transaction.
     *
     * @param saved Is saved or not.
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }


    /*
    ACTIONS
     */

    /**
     * Saves transaction.
     */
    public void save() {
        if (this.saved)
            throw new IllegalStateException("tried to save already saved transaction(" + this.uid + ")!");

        //Bson document saving.
        Objects.requireNonNull(DatabaseProvider.mongo().getCollection("bravo", "transactions", Transaction.class)).insertOne(this);

        this.saved = true;
    }


    /*
    CONVERTERS
     */

    /**
     * Converts transaction to a json.
     *
     * @return Json object.
     */
    @Nonnull
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", this.uid.toString());
        json.addProperty("date", this.date.getTime());
        json.addProperty("product", this.product);
        json.addProperty("buyer", this.buyer);
        json.addProperty("price", this.price);
        return json;
    }


    /*
    STATIC CONVERTERS
     */

    /**
     * Converts a json to a transaction.
     *
     * @param json Json object.
     * @return Transaction.
     */
    @Nonnull
    public static Transaction of(@Nonnull JsonObject json) {
        return new Transaction(
                json.get("product").getAsLong(),
                json.get("buyer").getAsLong(),
                json.get("price").getAsInt());
    }
}

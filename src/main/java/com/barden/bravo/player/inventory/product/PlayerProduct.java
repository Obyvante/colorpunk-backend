package com.barden.bravo.player.inventory.product;

import com.barden.bravo.player.Player;
import com.barden.bravo.product.Product;
import com.barden.bravo.product.ProductProvider;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class PlayerProduct {

    private final Player player;

    private final long id;
    private int amount;

    /**
     * Creates a player product.
     *
     * @param player Player.
     * @param id     Product id.
     * @param amount Product amount.
     */
    public PlayerProduct(@Nonnull Player player, long id, int amount) {
        this.player = Objects.requireNonNull(player);
        this.id = id;
        this.amount = amount;
    }

    /**
     * Gets product.
     *
     * @return Product.
     */
    @Nonnull
    public Product getProduct() {
        return ProductProvider.get(this.id);
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

    /**
     * Gets product id.
     *
     * @return Product id.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Gets product amount.
     *
     * @return Product amount.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Sets product amount.
     *
     * @param amount Product amount.
     */
    public void setAmount(int amount) {
        //Safety check.
        assert amount > 0 : "player(" + this.player.getId() + ") product(" + this.id + ") amount must be higher than 0!";
        var product = this.getProduct();
        if (amount > product.getCap())
            throw new IllegalStateException("player(" + this.player.getId() + ") product(" + this.id + ") amount must be lower than or equals to " + product.getCap());

        this.amount = amount;
    }

    /**
     * Adds product amount.
     *
     * @param amount Product amount.
     */
    public void addAmount(int amount) {
        //Safety check.
        assert amount > 0 : "player(" + this.player.getId() + ") product(" + this.id + ") amount must be higher than 0!";
        var product = this.getProduct();
        if (this.amount + amount > product.getCap())
            throw new IllegalStateException("player(" + this.player.getId() + ") product(" + this.id + ") amount must be lower than or equals to " + product.getCap());

        this.amount += amount;
    }

    /**
     * Removes product amount.
     *
     * @param amount Product amount.
     */
    public void removeAmount(int amount) {
        //Safety check.
        assert amount > 0 : "player(" + this.player.getId() + ") product(" + this.id + ") amount must be higher than 0!";
        assert this.amount - amount > 0 : "player(" + this.player.getId() + ") product(" + this.id + ") amount result must be higher than 0!";

        this.amount -= amount;
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player product to a json object.
     *
     * @return Player product json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", this.id);
        json.addProperty("amount", this.amount);
        return json;
    }

    /**
     * Converts player product to a bson document.
     *
     * @return Player product bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        document.put("id", new BsonInt64(this.id));
        document.put("amount", new BsonInt32(this.amount));
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player product.
     *
     * @param json Player product json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player product json object cannot be null!");

        this.setAmount(json.get("amount").getAsInt());
    }
}

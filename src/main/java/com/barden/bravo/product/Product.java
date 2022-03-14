package com.barden.bravo.product;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

/**
 * Product.
 */
public final class Product {

    private final long id;
    private final int cap;

    /**
     * Creates a product.
     *
     * @param id  Product id.
     * @param cap Product cap.
     */
    public Product(long id, int cap) {
        this.id = id;
        this.cap = cap;
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
     * Gets product cap. (MAXIMUM LIMIT)
     *
     * @return Product cap.
     */
    public int getCap() {
        return this.cap;
    }

    /**
     * Gets product as a json object.
     *
     * @return Product as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configure fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("cap", this.cap);

        //Returns created json object.
        return json_object;
    }
}

package com.barden.bravo.product;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

/**
 * Product.
 */
public final class Product {

    private final long id;
    private final String type;
    private final String name;
    private final boolean item;
    private final int cap;
    private final HashMap<String, String> metadata;

    /**
     * Creates a product.
     *
     * @param id       Product id.
     * @param name     Product name.
     * @param item     If product is an item or not. If it is not an item, it'll not add to your inventory.
     * @param cap      Product cap.
     * @param metadata Product metadata.
     */
    public Product(long id, @Nonnull String type, @Nonnull String name, boolean item, int cap, @Nonnull HashMap<String, String> metadata) {
        this.id = id;
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.item = item;
        this.cap = cap;
        this.metadata = Objects.requireNonNull(metadata);
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
     * Gets product type.
     *
     * @return Product type.
     */
    @Nonnull
    public String getType() {
        return this.type;
    }

    /**
     * Gets product name.
     *
     * @return Product name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Gets if product is an item or not.
     *
     * @return If product is an item or not.
     */
    public boolean isItem() {
        return this.item;
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
     * Gets product metadata.
     *
     * @return Product metadata.
     */
    @Nonnull
    public HashMap<String, String> getMetadata() {
        return this.metadata;
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
        json_object.addProperty("type", this.type);
        json_object.addProperty("name", this.name);
        json_object.addProperty("item", this.item);
        json_object.addProperty("cap", this.cap);

        JsonObject _metadata = new JsonObject();
        this.metadata.forEach(_metadata::addProperty);
        json_object.add("metadata", _metadata);

        //Returns created json object.
        return json_object;
    }
}

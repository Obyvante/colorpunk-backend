package com.barden.bravo.cosmetics.pet;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Pet class.
 */
public final class Pet {

    private final int id;
    private final String name;
    private final String assetId;

    /**
     * Creates pet object.
     *
     * @param id      Pet id.
     * @param name    Pet name.
     * @param assetId Pet asset id. (PACKAGE)
     */
    public Pet(int id, @Nonnull String name, @Nonnull String assetId) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.assetId = Objects.requireNonNull(assetId, "assetId cannot be null!");
    }

    /**
     * Gets pet id.
     *
     * @return Pet id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets name.
     *
     * @return Pet name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Gets asset id.
     *
     * @return Pet asset id. (PACKAGE)
     */
    @Nonnull
    public String getAssetId() {
        return this.assetId;
    }

    /**
     * Gets pet as a json object.
     *
     * @return Pet as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configure fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("name", this.name);
        json_object.addProperty("asset_id", this.assetId);

        //Returns created json object.
        return json_object;
    }
}

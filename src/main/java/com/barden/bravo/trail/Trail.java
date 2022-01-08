package com.barden.bravo.trail;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Trail abstract class.
 */
public class Trail {

    protected final int id;
    private final TrailType type;
    private final String name;
    private final String assetId;

    /**
     * Creates trail object.
     *
     * @param id      Trail id.
     * @param type    Trail type.
     * @param name    Trail name.
     * @param assetId Trail asset id. (PACKAGE)
     */
    public Trail(int id, @Nonnull TrailType type, @Nonnull String name, @Nonnull String assetId) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type cannot be null!");
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.assetId = Objects.requireNonNull(assetId, "assetId cannot be null!");
    }

    /**
     * Gets trail id.
     *
     * @return Trail id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets type.
     *
     * @return Trail type.
     */
    @Nonnull
    public TrailType getType() {
        return this.type;
    }

    /**
     * Gets name.
     *
     * @return Trail name.
     */
    @Nonnull
    public String getName() {
        return this.name;
    }

    /**
     * Gets asset id.
     *
     * @return Trail asset id. (PACKAGE)
     */
    @Nonnull
    public String getAssetId() {
        return this.assetId;
    }

    /**
     * Gets trail as a json object.
     *
     * @return Trail as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Configures fields.
        json_object.addProperty("id", this.id);
        json_object.addProperty("type", this.type.name());
        json_object.addProperty("name", this.name);
        json_object.addProperty("assetId", this.assetId);

        //Returns created json object.
        return json_object;
    }
}

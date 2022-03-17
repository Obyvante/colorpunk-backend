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
    private final String iconId;
    private final String meshId;
    private final String textureId;

    /**
     * Creates pet object.
     *
     * @param id        Pet id.
     * @param name      Pet name.
     * @param iconId    Pet icon id. (ROBLOX ASSET ID)
     * @param meshId    Pet mesh id. (ROBLOX ASSET ID)
     * @param textureId Pet texture id. (ROBLOX ASSET ID)
     */
    public Pet(int id, @Nonnull String name, @Nonnull String iconId, @Nonnull String meshId, @Nonnull String textureId) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name cannot be null!");
        this.iconId = Objects.requireNonNull(iconId, "icon id cannot be null!");
        this.meshId = Objects.requireNonNull(meshId, "mesh id cannot be null!");
        this.textureId = Objects.requireNonNull(textureId, "texture id cannot be null!");
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
     * Gets pet icon id.
     *
     * @return Pet icon id. (ROBLOX ASSET ID)
     */
    @Nonnull
    public String getIconId() {
        return this.iconId;
    }

    /**
     * Gets pet mesh id.
     *
     * @return Pet mesh id. (ROBLOX ASSET ID)
     */
    @Nonnull
    public String getMeshId() {
        return this.meshId;
    }

    /**
     * Gets pet texture id.
     *
     * @return Pet texture id. (ROBLOX ASSET ID)
     */
    @Nonnull
    public String getTextureId() {
        return this.textureId;
    }

    /**
     * Gets pet as a json object.
     *
     * @return Pet as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json = new JsonObject();

        //Configure fields.
        json.addProperty("id", this.id);
        json.addProperty("name", this.name);
        json.addProperty("iconId", this.iconId);
        json.addProperty("meshId", this.meshId);
        json.addProperty("textureId", this.textureId);

        //Returns created json object.
        return json;
    }
}

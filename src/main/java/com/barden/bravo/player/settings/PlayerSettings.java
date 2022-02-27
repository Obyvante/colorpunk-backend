package com.barden.bravo.player.settings;

import com.barden.bravo.player.Player;
import com.barden.bravo.player.settings.type.PlayerSettingType;
import com.google.gson.JsonObject;
import org.bson.BsonDocument;
import org.bson.BsonDouble;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;

//Player settings class.
@SuppressWarnings("unused")
public final class PlayerSettings {

    private final Player player;
    private final HashMap<PlayerSettingType, Integer> content = new HashMap<>();

    /**
     * Creates a player settings.
     *
     * @param player Player.
     */
    public PlayerSettings(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates a player settings from a bson document.
     *
     * @param player   Player.
     * @param document Player settings bson document.
     */
    public PlayerSettings(@Nonnull Player player, @Nonnull BsonDocument document) {
        //Objects null check.
        Objects.requireNonNull(document, "settings bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares settings from the declared bson document.
        document.forEach((key, value) -> this.content.put(PlayerSettingType.valueOf(key), value.asInt32().intValue()));
    }

    /**
     * Gets player setting value.
     *
     * @param setting Player setting.
     * @return Player setting value.
     */
    public int get(@Nonnull PlayerSettingType setting) {
        return this.content.getOrDefault(Objects.requireNonNull(setting, "setting cannot be null!"), setting.getDefaultValue());
    }

    /**
     * Gets player setting value as a boolean.
     *
     * @param setting Player setting.
     * @return Player setting value as a boolean.
     */
    public boolean asBoolean(@Nonnull PlayerSettingType setting) {
        return this.get(setting) == 1;
    }

    /**
     * Gets player setting value as an enum.
     *
     * @param setting Player setting.
     * @param enums   Enums.
     * @param <T>     Enum type.
     * @return Player setting value as an enum.
     */
    @Nonnull
    public <T> T getAsEnum(@Nonnull PlayerSettingType setting, @Nonnull T[] enums) {
        return Objects.requireNonNull(enums, "enums cannot be null!")[this.get(setting)];
    }

    /**
     * Sets player setting value.
     *
     * @param setting Player setting.
     * @param value   Player setting value.
     * @return Player settings. (BUILDER)
     */
    @Nonnull
    public PlayerSettings set(@Nonnull PlayerSettingType setting, int value) {
        this.content.put(Objects.requireNonNull(setting, "setting cannot be null!"), value);
        return this;
    }


    /*
    CONVERTERS
     */

    /**
     * Converts player settings as a json object.
     *
     * @return Player settings json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        this.content.forEach((stat, value) -> json.addProperty(stat.name(), value));
        return json;
    }

    /**
     * Converts player settings to a bson document.
     *
     * @return Player settings bson document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        BsonDocument document = new BsonDocument();
        this.content.forEach((stat, value) -> document.put(stat.name(), new BsonDouble(value)));
        return document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player settings.
     *
     * @param json Json object.
     */
    public void update(@Nonnull JsonObject json) {
        //Objects null check.
        Objects.requireNonNull(json, "player settings json object cannot be null!");

        //Clears all player settings to make sure it won't have existed player setting.
        this.content.clear();

        json.entrySet().forEach((entry) -> this.content.put(PlayerSettingType.valueOf(entry.getKey()), entry.getValue().getAsInt()));
    }
}
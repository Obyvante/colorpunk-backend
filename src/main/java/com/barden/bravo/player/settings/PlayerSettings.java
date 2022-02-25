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
    private HashMap<PlayerSettingType, Integer> settings = new HashMap<>();

    /**
     * Creates player settings object.
     *
     * @param player Player.
     */
    public PlayerSettings(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player, "player cannot be null!");
    }

    /**
     * Creates player settings object.
     *
     * @param player       Player.
     * @param bsonDocument Bson document. (MONGO)
     */
    public PlayerSettings(@Nonnull Player player, @Nonnull BsonDocument bsonDocument) {
        //Objects null check.
        Objects.requireNonNull(bsonDocument, "settings bson document cannot be null!");

        this.player = Objects.requireNonNull(player, "player cannot be null!");

        //Declares settings from the declared bson document.
        bsonDocument.forEach((key, value) -> this.settings.put(PlayerSettingType.valueOf(key), value.asInt32().intValue()));
    }

    /**
     * Gets setting value.
     *
     * @param setting Player setting.
     * @return Setting value.
     */
    public int get(@Nonnull PlayerSettingType setting) {
        return this.settings.getOrDefault(Objects.requireNonNull(setting, "setting cannot be null!"), setting.getDefaultValue());
    }

    /**
     * Gets setting value as boolean.
     *
     * @param setting Player setting.
     * @return Setting value as boolean.
     */
    public boolean getAsBoolean(@Nonnull PlayerSettingType setting) {
        return this.get(setting) == 1;
    }

    /**
     * Gets setting value as enum.
     *
     * @param setting Player setting.
     * @param enums   Enums.
     * @param <T>     Enum type.
     * @return Setting value as enum.
     */
    @Nonnull
    public <T> T getAsEnum(@Nonnull PlayerSettingType setting, @Nonnull T[] enums) {
        //Objects null check.
        Objects.requireNonNull(enums, "enums cannot be null!");
        return enums[this.get(setting)];
    }

    /**
     * Sets setting value.
     *
     * @param setting Player setting.
     * @param value   Setting value.
     * @return Player settings.
     */
    @Nonnull
    public PlayerSettings set(@Nonnull PlayerSettingType setting, int value) {
        //Objects null check.
        Objects.requireNonNull(setting, "setting cannot be null!");

        //Sets setting value.
        this.settings.put(setting, value);
        return this;
    }


    /*
    CONVERTERS
     */

    /**
     * Gets player settings as a json object.
     *
     * @return Statistics as a json object.
     */
    @Nonnull
    public JsonObject toJsonObject() {
        //Creates json object.
        JsonObject json_object = new JsonObject();

        //Adds settings to created empty json object.
        this.settings.forEach((stat, value) -> json_object.addProperty(stat.name(), value));

        //Returns created json object.
        return json_object;
    }

    /**
     * Converts player settings object to document. (MONGO BSON)
     *
     * @return Statistics document.
     */
    @Nonnull
    public BsonDocument toBsonDocument() {
        //Creates empty bson document.
        BsonDocument bson_document = new BsonDocument();

        //Adds settings to created empty bson document.
        this.settings.forEach((stat, value) -> bson_document.put(stat.name(), new BsonDouble(value)));

        //Returns created bson document.
        return bson_document;
    }


    /*
    MEMORY
     */

    /**
     * Updates player settings object.
     *
     * @param json_object Json object.
     */
    public void update(@Nonnull JsonObject json_object) {
        //Objects null check.
        Objects.requireNonNull(json_object, "player settings json object cannot be null!");

        //Resets settings.
        this.settings = new HashMap<>();

        //Adds settings from json object one by one to settings object.
        json_object.entrySet().forEach((entry) -> this.settings.put(PlayerSettingType.valueOf(entry.getKey()), entry.getValue().getAsInt()));
    }
}
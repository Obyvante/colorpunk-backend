package com.barden.bravo.player.http;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerProvider;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

/**
 * Player HTTP functionality class.
 */
public final class PlayerHTTPFunctionality {

    /**
     * Results.
     */
    private enum Result {
        PLAYER_NOT_FOUND,
        PLAYER_NOT_FOUND_IN_CACHE,
        INVALID_USER_ID,
        INVALID_JSON_OBJECT
    }

    /**
     * Gets player by its id.
     * It will only check cache, not database.
     * Therefore, make sure you are searching for
     * players in the cache.
     *
     * @param id Roblox user id.
     * @return Json object. (CONFIGURED BASED ON RESPONSE ENTITY)
     */
    @Nonnull
    public static JsonObject getById(@Nonnull String id) {
        //Declares base fields.
        JsonObject json;
        boolean success = true;
        long user_id = -1;

        //Handles success.
        if (id.isEmpty()) { //If is empty.
            success = false;
        } else { //Otherwise.
            //Handles number exception.
            try {
                user_id = Long.parseLong(id);
            } catch (Exception exception) {
                success = false;
            }
        }

        //Handles success.
        if (!success) {
            //Creates json object.
            json = HTTPResponse.of(false, Result.INVALID_USER_ID);
        } else {
            //Gets player field.
            Optional<Player> player = PlayerProvider.find(user_id);
            //Creates json object.
            json = HTTPResponse.of(player.isPresent(), Result.PLAYER_NOT_FOUND_IN_CACHE);
            //If player is present, adds to the results.
            player.ifPresent(value -> json.add("results", value.toJsonObject()));
        }

        //Returns create json object.
        return json;
    }

    /**
     * Updates player by its id.
     *
     * @param json Player json object.
     * @return Update result.
     */
    @Nonnull
    public static JsonObject updateById(@Nonnull JsonObject json) {
        //Objects check null.
        Objects.requireNonNull(json, "player json object cannot be null!");

        //If json is not valid, returns not successful response entity.
        if (json.isJsonNull() || !json.has("id"))
            return HTTPResponse.of(false, Result.INVALID_JSON_OBJECT);

        //Handles json exceptions.
        try {
            //Declares user id.
            long user_id = json.get("id").getAsLong();

            //Gets player from the cache.
            Player player = PlayerProvider.find(user_id).orElse(null);
            //If player does not exist, returns not successful response entity.
            if (player == null) {
                try {
                    player = PlayerProvider.handle(user_id, "", false);
                } catch (Exception exception) {
                    return HTTPResponse.of(false, Result.PLAYER_NOT_FOUND);
                }
            }

            //Updates player cache with declared json object.
            player.update(json);

            //Returns success
            return HTTPResponse.of(true);
        } catch (Exception exception) {
            return HTTPResponse.of(false, Result.INVALID_JSON_OBJECT);
        }
    }

    /**
     * Updates players.
     *
     * @param json Players json object.
     * @return Update result.
     */
    @Nonnull
    public static JsonObject update(@Nonnull JsonObject json) {
        //Objects check null.
        Objects.requireNonNull(json, "player json object cannot be null!");

        //If json is not valid, returns not successful response entity.
        if (json.isJsonNull() || json.keySet().size() == 0)
            return HTTPResponse.of(false, Result.INVALID_JSON_OBJECT);

        //Handles json exceptions.
        try {
            //Loops through player json objects.
            json.entrySet().forEach(entry -> {
                //Declares player json object.
                JsonObject player_json = entry.getValue().getAsJsonObject();

                //Declares user id.
                long user_id = Long.parseLong(entry.getKey());

                //Gets player from the cache.
                Player player = PlayerProvider.find(user_id).orElse(null);
                //If player does not exist, returns not successful response entity.
                if (player == null) {
                    try {
                        player = PlayerProvider.handle(user_id, "", false);
                    } catch (Exception exception) {
                        return;
                    }
                }

                //Updates player cache with declared json object.
                player.update(player_json);
            });

            //Returns success
            return HTTPResponse.of(true);
        } catch (Exception exception) {
            return HTTPResponse.of(false, Result.INVALID_JSON_OBJECT);
        }
    }

    /**
     * Handles player on both cache and database.
     * <p>
     * HOW IT WORKS?
     * <p>
     * If player is already exist in cache, it returns it.
     * If not, it will try to find player in database, if
     * it finds, it will return. If not, it will create new
     * player object then save it to the database and cache
     * then returns it.
     *
     * @param id     Roblox user id.
     * @param name   Roblox name.
     * @param insert Should insert new player to the database if it is not exist.
     * @return Response json object.
     */
    @Nonnull
    public static JsonObject handle(@Nonnull String id, @Nonnull String name, boolean insert) {
        //Objects null check.
        Objects.requireNonNull(id, "roblox user id cannot be null!");

        //Declares base fields.
        boolean success = true;
        long user_id = -1;

        //Handles success.
        if (id.isEmpty()) { //If is empty.
            success = false;
        } else { //Otherwise.
            //Handles number exception.
            try {
                user_id = Long.parseLong(id);
            } catch (Exception exception) {
                success = false;
            }
        }

        //Creates json object.
        JsonObject json;

        //If it is successful, adds player json object as a results.
        if (success) {
            try {
                json = HTTPResponse.of(true);
                json.add("results", PlayerProvider.handle(user_id, name, insert).toJsonObject()); // Player provider will handle all heavy work.
            } catch (Exception exception) {
                json = HTTPResponse.of(false, Result.INVALID_JSON_OBJECT);
            }
        } else {
            json = HTTPResponse.of(false, Result.INVALID_USER_ID);
        }

        //Returns configured json object.
        return json;
    }
}
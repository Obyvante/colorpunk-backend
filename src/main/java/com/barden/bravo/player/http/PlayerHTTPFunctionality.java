package com.barden.bravo.player.http;

import com.barden.bravo.http.HTTPRepository;
import com.barden.bravo.player.Player;
import com.barden.bravo.player.PlayerRepository;
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
        JsonObject json_object;
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
            json_object = HTTPRepository.createResponse(false, Result.INVALID_USER_ID);
        } else {
            //Gets player field.
            Optional<Player> player = PlayerRepository.find(user_id);
            //Creates json object.
            json_object = HTTPRepository.createResponse(player.isPresent(), Result.PLAYER_NOT_FOUND);
            //If player is present, adds to the results.
            player.ifPresent(value -> json_object.add("results", value.toJsonObject()));
        }

        //Returns create json object.
        return json_object;
    }

    /**
     * Updates player by its id.
     *
     * @param json_object Update result.
     */
    @Nonnull
    public static JsonObject updateById(@Nonnull JsonObject json_object) {
        //Objects check null.
        Objects.requireNonNull(json_object, "player json object cannot be null!");

        //If json is not valid, returns not successful response entity.
        if (json_object.isJsonNull() || !json_object.has("id"))
            return HTTPRepository.createResponse(false, Result.INVALID_JSON_OBJECT);

        //Handles json exceptions.
        try {
            //Declares user id.
            long user_id = json_object.get("id").getAsLong();

            //Gets player from the cache.
            Player player = PlayerRepository.find(user_id).orElse(null);
            //If player does not exist, returns not successful response entity.
            if (player == null)
                return HTTPRepository.createResponse(false, Result.PLAYER_NOT_FOUND_IN_CACHE);

            //Updates player cache with declared json object.
            player.update(json_object);

            //Returns success
            return HTTPRepository.createResponse(true);
        } catch (Exception exception) {
            return HTTPRepository.createResponse(false, Result.INVALID_JSON_OBJECT);
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
     * @param id Roblox user id.
     * @return Response json object.
     */
    @Nonnull
    public static JsonObject handle(@Nonnull String id) {
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
        JsonObject json_object = HTTPRepository.createResponse(success, Result.INVALID_USER_ID);

        //If it is successful, adds player json object as a results.
        if (success)
            json_object.add("results", PlayerRepository.handle(user_id).toJsonObject()); // Player repository will handle all heavy work.

        //Returns configured json object.
        return json_object;
    }

}

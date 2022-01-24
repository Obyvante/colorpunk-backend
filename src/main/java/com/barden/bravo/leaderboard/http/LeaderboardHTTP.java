package com.barden.bravo.leaderboard.http;

import com.barden.bravo.http.HTTPRepository;
import com.barden.bravo.leaderboard.Leaderboard;
import com.barden.bravo.leaderboard.LeaderboardRepository;
import com.barden.bravo.leaderboard.enums.LeaderboardType;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Leaderboard HTTP class.
 */
@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardHTTP {

    /**
     * Results.
     */
    private enum Result {
        INVALID_LEADERBOARD_TYPE
    }

    /**
     * Gets leaderboard by its type.
     *
     * @param type Leaderboard type.
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> getByType(@RequestParam @Nonnull String type) {
        //Objects null check.
        Objects.requireNonNull(type, "leaderboard type cannot be null!");

        //Creates answer object.
        JsonObject json_object;

        //Handles exceptions.
        try {
            //Gets leaderboard.
            Leaderboard leaderboard = LeaderboardRepository.get(LeaderboardType.valueOf(type));

            //Creates json object.
            json_object = HTTPRepository.createResponse(true);
            json_object.add("results", leaderboard.toJsonObject());

            //Returns response entity.
            return new ResponseEntity<>(json_object, HttpStatus.OK);
        } catch (Exception exception) {
            json_object = HTTPRepository.createResponse(false, Result.INVALID_LEADERBOARD_TYPE);
        }

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

}

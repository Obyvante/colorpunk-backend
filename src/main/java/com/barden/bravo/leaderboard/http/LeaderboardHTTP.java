package com.barden.bravo.leaderboard.http;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.leaderboard.Leaderboard;
import com.barden.bravo.leaderboard.LeaderboardProvider;
import com.barden.bravo.player.statistics.type.PlayerStatisticType;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.database.DatabaseProvider;
import com.barden.library.scheduler.SchedulerProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.HashMap;

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
        INVALID_JSON_STRUCTURE
    }

    /**
     * Gets leaderboard.
     *
     * @param body Leaderboard information.
     * @return Response entity. (JSON OBJECT)
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<JsonObject>> get(@RequestBody JsonObject body) {
        //Creates deferred result.
        DeferredResult<ResponseEntity<JsonObject>> result = new DeferredResult<>();

        //Safet check.
        if (body == null || body.isJsonNull() || body.entrySet().isEmpty()) {
            result.setResult(new ResponseEntity<>(HTTPResponse.of(false, Result.INVALID_JSON_STRUCTURE), HttpStatus.OK));
            return result;
        }

        //Handles task.
        SchedulerProvider.schedule(task -> {
            JsonObject json = HTTPResponse.of(true, Result.INVALID_JSON_STRUCTURE);
            JsonObject ranks_json = new JsonObject();

            //Handles exceptions.
            try {
                //Declares required fields.
                PlayerStatisticType type = PlayerStatisticType.valueOf(body.get("type").getAsString());

                //Gets leaderboard.
                Leaderboard leaderboard = LeaderboardProvider.get(type);

                //Handles player rank requests.
                if (body.keySet().contains("players")) {
                    //Declares required fields.
                    JsonArray players_json = body.getAsJsonArray("players");
                    HashMap<String, Response<Long>> rank_responses = new HashMap<>();

                    //Handles redis exception.
                    try (Jedis resource = DatabaseProvider.redis().getClient().getResource()) {
                        //Creates pipeline.
                        Pipeline pipeline = resource.pipelined();
                        //Fetches player ranks from the leaderboard.
                        players_json.forEach(player -> rank_responses.put(player.getAsString(), pipeline.zrevrank("leaderboard:" + type, player.getAsString())));
                        //Executes pipeline.
                        pipeline.sync();
                    }

                    //Writes player ranks to the rank json.
                    rank_responses.forEach((key, value) -> ranks_json.addProperty(key, value.get() + 1));

                    //Adds response to the base json.
                    json.add("responses", ranks_json);
                }

                //Creates json object.
                json.add("results", leaderboard.toJsonObject());
            } catch (Exception exception) {
                //Responses request to avoid long waiting durations.
                result.setResult(new ResponseEntity<>(HTTPResponse.of(false), HttpStatus.OK));

                //Informs server about the exception. It might be important.
                BardenJavaLibrary.getLogger().error("Couldn't process leaderboard!", exception);
                return;
            }

            //Sets result.
            result.setResult(new ResponseEntity<>(json, HttpStatus.OK));
        });

        //Returns response entity.
        return result;
    }
}

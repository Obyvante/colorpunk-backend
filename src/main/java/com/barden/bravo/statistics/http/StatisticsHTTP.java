package com.barden.bravo.statistics.http;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.statistics.updater.StatisticsUpdater;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Statistics HTTP class.
 */
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsHTTP {

    /**
     * Results.
     */
    private enum Result {
        INVALID_JSON_OBJECT
    }

    /**
     * Updates statistics.
     *
     * @param json_object Statistics json object.
     * @return Update result.
     */
    @PostMapping(value = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> update(@RequestBody JsonObject json_object) {
        //If json is not valid, returns not successful response entity.
        if (json_object == null || json_object.isJsonNull() || json_object.keySet().size() == 0)
            return new ResponseEntity<>(HTTPResponse.of(false, Result.INVALID_JSON_OBJECT), HttpStatus.OK);

        //Adds json object to the statistics queue.
        StatisticsUpdater.addQueue(json_object);

        //Returns response entity.
        return new ResponseEntity<>(HTTPResponse.of(true), HttpStatus.OK);
    }
}

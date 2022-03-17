package com.barden.bravo.metrics.http;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.metrics.GlobalMetrics;
import com.barden.bravo.metrics.PlayerMetrics;
import com.barden.library.BardenJavaLibrary;
import com.barden.library.scheduler.SchedulerProvider;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Metric HTTP class.
 */
@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsHTTP {

    /**
     * Results.
     */
    private enum Result {
        INVALID_JSON_STRUCTURE
    }

    /**
     * Writes sent metrics.
     *
     * @param json Metric json.
     * @return Update result.
     */
    @PostMapping(value = "/write", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<JsonObject>> update(@RequestBody JsonObject json) {
        //Creates deferred result.
        DeferredResult<ResponseEntity<JsonObject>> result = new DeferredResult<>();

        //Safet check.
        if (json == null || json.isJsonNull() || json.entrySet().isEmpty()) {
            result.setResult(new ResponseEntity<>(HTTPResponse.of(false, Result.INVALID_JSON_STRUCTURE), HttpStatus.OK));
            return result;
        }

        //Handles task.
        SchedulerProvider.schedule(task -> {
            try {
                //Handles player metrics.
                if (json.has("players")) {
                    var players_json = json.getAsJsonObject("players");
                    if (players_json != null && !players_json.isJsonNull() && !players_json.entrySet().isEmpty())
                        PlayerMetrics.write(players_json);
                }

                //Handles global metrics.
                if (json.has("global")) {
                    var global_json = json.getAsJsonObject("global");
                    if (global_json != null && !global_json.isJsonNull() && !global_json.entrySet().isEmpty())
                        GlobalMetrics.write(global_json);
                }
            } catch (Exception exception) {
                //Responses request to avoid long waiting durations.
                result.setResult(new ResponseEntity<>(HTTPResponse.of(false), HttpStatus.OK));

                //Informs server about the exception. It might be important.
                BardenJavaLibrary.getLogger().error("Couldn't wrote metrics!", exception);
                return;
            }

            //Sets result.
            result.setResult(new ResponseEntity<>(HTTPResponse.of(true), HttpStatus.OK));
        });

        //Returns response entity.
        return result;
    }
}

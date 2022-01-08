package com.barden.bravo.player.http;

import com.barden.library.scheduler.SchedulerRepository;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nonnull;

/**
 * Player HTTP class.
 */
@RestController
@RequestMapping("/api/v1/player")
public class PlayerHTTP {

    /**
     * Gets player by its id.
     * It will only check cache, not database.
     * Therefore, make sure you are searching for
     * players in the cache.
     *
     * @param id Roblox user id.
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> getById(@Nonnull @RequestParam String id) {
        return new ResponseEntity<>(PlayerHTTPFunctionality.getById(id), HttpStatus.OK);
    }

    /**
     * Updates player by its id.
     *
     * @param json_object Player json object.
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> updateById(@Nonnull @RequestBody JsonObject json_object) {
        return new ResponseEntity<>(PlayerHTTPFunctionality.updateById(json_object), HttpStatus.OK);
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
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(value = "/handle", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<JsonObject>> handle(@Nonnull @RequestParam String id) {
        //Creates deferred result.
        DeferredResult<ResponseEntity<JsonObject>> result = new DeferredResult<>();
        //Handles task.
        SchedulerRepository.schedule(task -> result.setResult(new ResponseEntity<>(PlayerHTTPFunctionality.handle(id), HttpStatus.OK)));
        //Returns response entity.
        return result;
    }

}

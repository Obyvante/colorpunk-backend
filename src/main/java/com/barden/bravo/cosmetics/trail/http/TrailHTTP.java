package com.barden.bravo.cosmetics.trail.http;

import com.barden.bravo.cosmetics.trail.Trail;
import com.barden.bravo.cosmetics.trail.TrailProvider;
import com.barden.bravo.http.HTTPResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

/**
 * Trail HTTP class.
 */
@RestController
@RequestMapping("/api/v1/trail")
public final class TrailHTTP {

    /**
     * Results.
     */
    private enum Result {
        PET_NOT_FOUND
    }

    /**
     * Gets all trails.
     *
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> get() {
        //Gets trail.
        Set<Trail> trails = TrailProvider.getContent();

        //Creates json object.
        JsonObject json_object = new JsonObject();
        json_object.addProperty("success", true);

        //Adds to the results.
        JsonArray results = new JsonArray();
        trails.forEach(trail -> results.add(trail.toJsonObject()));
        json_object.add("results", results);

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

    /**
     * Gets trail by its id.
     *
     * @param id Trail id.
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> getById(@RequestParam int id) {
        //Gets trail field.
        Optional<Trail> trail = TrailProvider.find(id);

        //Creates json object.
        JsonObject json_object = HTTPResponse.of(trail.isPresent(), Result.PET_NOT_FOUND);
        //If trail is present, adds to the results.
        trail.ifPresent(value -> json_object.add("results", value.toJsonObject()));

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

}

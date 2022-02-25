package com.barden.bravo.cosmetics.pet.http;

import com.barden.bravo.cosmetics.pet.Pet;
import com.barden.bravo.cosmetics.pet.PetProvider;
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
 * Pet HTTP class.
 */
@RestController
@RequestMapping("/api/v1/pet")
public class PetHTTP {

    /**
     * Results.
     */
    private enum Result {
        PET_NOT_FOUND
    }

    /**
     * Gets all pets.
     *
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> get() {
        //Gets pet.
        Set<Pet> pets = PetProvider.getContent();

        //Creates json object.
        JsonObject json_object = new JsonObject();
        json_object.addProperty("success", true);

        //Adds to the results.
        JsonArray results = new JsonArray();
        pets.forEach(pet -> results.add(pet.toJsonObject()));
        json_object.add("results", results);

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

    /**
     * Gets pet by its id.
     *
     * @param id Pet id.
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> getById(@RequestParam int id) {
        //Gets pet field.
        Optional<Pet> pet = PetProvider.find(id);

        //Creates json object.
        JsonObject json_object = HTTPResponse.of(pet.isPresent(), Result.PET_NOT_FOUND);
        //If pet is present, adds to the results.
        pet.ifPresent(value -> json_object.add("results", value.toJsonObject()));

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

}

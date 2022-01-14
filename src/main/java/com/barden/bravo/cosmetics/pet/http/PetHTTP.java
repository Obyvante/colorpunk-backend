package com.barden.bravo.cosmetics.pet.http;

import com.barden.bravo.cosmetics.pet.Pet;
import com.barden.bravo.cosmetics.pet.PetRepository;
import com.barden.bravo.http.HTTPRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;

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
        HashSet<Pet> pets = PetRepository.getContent();

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
        Optional<Pet> pet = PetRepository.find(id);

        //Creates json object.
        JsonObject json_object = HTTPRepository.createResponse(pet.isPresent(), Result.PET_NOT_FOUND);
        //If pet is present, adds to the results.
        pet.ifPresent(value -> json_object.add("results", value.toJsonObject()));

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

}

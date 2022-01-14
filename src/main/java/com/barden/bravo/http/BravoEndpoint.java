package com.barden.bravo.http;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bravo endpoint class.
 */
@RestController
public class BravoEndpoint {

    /**
     * Handles endpoint request.
     *
     * @return Response. (JSON OBJECT)
     */
    @GetMapping
    public ResponseEntity<JsonObject> handle() {
        return new ResponseEntity<>(HTTPRepository.createResponse(false), HttpStatus.NOT_FOUND);
    }

}

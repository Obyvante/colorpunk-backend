package com.barden.bravo.health;

import com.barden.bravo.http.HTTPResponse;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health HTTP class.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthHTTP {

    /**
     * Gets server health check.
     *
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping
    public ResponseEntity<JsonObject> get() {
        return new ResponseEntity<>(HTTPResponse.of(true), HttpStatus.OK);
    }

}

package com.barden.bravo.product.http;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.product.Product;
import com.barden.bravo.product.ProductProvider;
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
 * Product HTTP class.
 */
@RestController
@RequestMapping("/api/v1/product")
public class ProductHTTP {

    /**
     * Results.
     */
    private enum Result {
        PET_NOT_FOUND
    }

    /**
     * Gets all products.
     *
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> get() {
        //Gets product.
        Set<Product> products = ProductProvider.getContent();

        //Creates json object.
        JsonObject json_object = new JsonObject();
        json_object.addProperty("success", true);

        //Adds to the results.
        JsonArray results = new JsonArray();
        products.forEach(product -> results.add(product.toJsonObject()));
        json_object.add("results", results);

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

    /**
     * Gets product by its id.
     *
     * @param id Product id.
     * @return Response entity. (JSON OBJECT)
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> getById(@RequestParam int id) {
        //Gets product field.
        Optional<Product> product = ProductProvider.find(id);

        //Creates json object.
        JsonObject json_object = HTTPResponse.of(product.isPresent(), Result.PET_NOT_FOUND);
        //If product is present, adds to the results.
        product.ifPresent(value -> json_object.add("results", value.toJsonObject()));

        //Returns response entity.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

}

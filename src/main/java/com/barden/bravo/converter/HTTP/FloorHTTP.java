package com.barden.bravo.converter.HTTP;

import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.http.HTTPResponse;
import com.barden.bravo.http.HTTPValidation;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

@RestController
@RequestMapping("/api/image/converter")
public class FloorHTTP {

    /**
     * Results.
     */
    public enum Result {
        INVALID_FILE
    }

    /**
     * Gets all pets.
     *
     * @return Response entity. (JSON OBJECT)
     */
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonObject> get(@RequestBody byte[] bytes) throws Exception {
        //If byte list is empty, no need to continue.
        if (bytes== null || bytes.length == 0)
            return new ResponseEntity<>(HTTPResponse.of(false, Result.INVALID_FILE), HttpStatus.OK);

        //Converts request to an image.
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));

        //Creates json object.
        JsonObject json_object = new JsonObject();
        //Creates parts object.
        JsonObject parts = new JsonObject();

        //Loops through pixels.
        for (int x = 1; x <= 48; x++) {
            JsonObject x_object = new JsonObject();
            int c_coordinate = x - 1;
            for (int y = 1; y <= 48; y++) {
                JsonObject y_object = new JsonObject();
                JsonObject color_object = new JsonObject();

                int color = image.getRGB(c_coordinate, y - 1);
                int red = (color >> 16) & 0xff;
                int green = (color >> 8) & 0xff;
                int blue = (color) & 0xff;

                color_object.addProperty("r", red);
                color_object.addProperty("g", green);
                color_object.addProperty("b", blue);

                y_object.add("color", color_object);
                x_object.add(String.valueOf(y), y_object);
            }

            parts.add(String.valueOf(x), x_object);
        }

        //Creates part.
        json_object.add("parts", parts);

        //Returns created json object.
        return new ResponseEntity<>(json_object, HttpStatus.OK);
    }

}

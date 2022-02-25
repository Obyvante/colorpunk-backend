package com.barden.bravo.http;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * HTTP Response class.
 */
public final class HTTPResponse {

    /**
     * Creates response.
     *
     * @param success Is success or not.
     * @return Response. (JSON OBJECT)
     */
    @Nonnull
    public static JsonObject of(boolean success) {
        //Creates json object.
        JsonObject json_object = new JsonObject();
        //Configures json object.
        json_object.addProperty("success", success);
        //Returns created json object.
        return json_object;
    }

    /**
     * Creates response.
     *
     * @param success    Is success or not.
     * @param enumObject Enum.
     * @return Response. (JSON OBJECT)
     */
    @Nonnull
    public static JsonObject of(boolean success, @Nonnull Enum<?> enumObject) {
        //Creates json object.
        JsonObject json_object = new JsonObject();
        //Configures json object.
        json_object.addProperty("success", success);
        //If it is not successfully, adds error field.
        if (!success)
            json_object.addProperty("error", Objects.requireNonNull(enumObject, "enum object cannot be null!").name());
        //Returns created json object.
        return json_object;
    }
}

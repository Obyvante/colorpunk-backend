package com.barden.bravo.http;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * HTTP Response class.
 */
public final class HTTPResponse {

    /**
     * Creates a response.
     *
     * @param success Is success or not.
     * @return Response. (JSON OBJECT)
     */
    @Nonnull
    public static JsonObject of(boolean success) {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("success", success);
        return json_object;
    }

    /**
     * Creates a response.
     *
     * @param success    Is success or not.
     * @param enumObject Enum.
     * @return Response. (JSON OBJECT)
     */
    @Nonnull
    public static JsonObject of(boolean success, @Nonnull Enum<?> enumObject) {
        JsonObject json_object = new JsonObject();
        json_object.addProperty("success", success);
        if (!success)
            json_object.addProperty("error", Objects.requireNonNull(enumObject, "enum object cannot be null!").name());
        return json_object;
    }
}

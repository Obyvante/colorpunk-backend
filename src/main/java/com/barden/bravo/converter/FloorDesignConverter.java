package com.barden.bravo.converter;

import com.barden.library.BardenJavaLibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * Floor design converter class.
 */
public final class FloorDesignConverter {

    /**
     * Converts image to a floor json.
     *
     * @param name Image path.
     * @param path Convert path.
     */
    public static void convert(@Nonnull String name, @Nonnull String path) {
        //Objects null check.
        Objects.requireNonNull(path, "path cannot be null!");

        //Declares required fields.
        BufferedImage image;

        //Handles exception.
        try {
            image = ImageIO.read(Objects.requireNonNull(FloorDesignConverter.class.getResourceAsStream("/" + name)));
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("error!", exception);
            return;
        }

        //Creates json object.
        JsonObject json_object = new JsonObject();
        //Creates parts object.
        JsonObject parts = new JsonObject();

        //Loops through pixels.
        for (int x = 1; x <= 48; x++) {
            JsonObject x_object = new JsonObject();
            for (int y = 1; y <= 48; y++) {
                JsonObject y_object = new JsonObject();
                JsonObject color_object = new JsonObject();

                int color = image.getRGB(x - 1, y - 1);
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

        File file = new File("/exports/floors/" + path);
        file.getParentFile().mkdirs();

        try (Writer writer = new FileWriter(path)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(json_object, writer);
        } catch (Exception exception) {
            BardenJavaLibrary.getLogger().error("Couldn't convert image!", exception);
        }
    }

}

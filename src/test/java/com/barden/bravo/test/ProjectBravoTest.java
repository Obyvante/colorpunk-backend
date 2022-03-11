package com.barden.bravo.test;

import com.google.gson.JsonObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.lang.NonNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Project bravo class. (MAIN) [TEST]
 */
@SpringBootApplication(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        RedisAutoConfiguration.class,
        RedisReactiveAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        InfluxDbAutoConfiguration.class
})
public class ProjectBravoTest {

    /**
     * Runs project bravo as a test.
     *
     * @param arguments Arguments.
     */
    public static void main(@NonNull String[] arguments) {
        JsonObject object = new JsonObject();
        for (int i = 0; i < 10000; i++) {
            JsonObject json = new JsonObject();
            json.addProperty("ROBLOX_SPENT", ThreadLocalRandom.current().nextInt(0, 999999));
            json.addProperty("WIN", ThreadLocalRandom.current().nextInt(0, 999999));
            object.add(String.valueOf(i), json);
        }
        System.out.println(object);
    }
}

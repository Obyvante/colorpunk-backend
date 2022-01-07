package com.barden.bravo.settings;

import com.barden.library.file.TomlFileLoader;

import javax.annotation.Nonnull;

/**
 * Settings class.
 */
public final class Settings {
    private static String key = "";

    /**
     * Initializes settings object.
     */
    public static void initialize() {
        //Handles configuration.
        TomlFileLoader.getConfig("settings", true).ifPresent(file -> {
            //Sets key.
            key = file.get("security.key");
        });
    }

    /**
     * Gets key.
     *
     * @return key.
     */
    @Nonnull
    public static String getKey() {
        return key;
    }
}

package com.barden.bravo.player.currencies;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Player current types.
 */
public enum PlayerCurrencyType {
    GOLD("Gold");

    private final String name;

    /**
     * Creates currency object.
     *
     * @param name PlayerCurrencyType name.
     */
    PlayerCurrencyType(@NonNull String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null!");
    }

    /**
     * Gets name.
     *
     * @return PlayerCurrencyType name.
     */
    @NonNull
    public String getName() {
        return this.name;
    }
}

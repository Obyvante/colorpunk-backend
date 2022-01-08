package com.barden.bravo.currency;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Currencies.
 */
public enum Currency {
    GOLD("Gold");

    private final String name;

    /**
     * Creates currency object.
     *
     * @param name Currency name.
     */
    Currency(@NonNull String name) {
        this.name = Objects.requireNonNull(name, "name cannot be null!");
    }

    /**
     * Gets name.
     *
     * @return Currency name.
     */
    @NonNull
    public String getName() {
        return this.name;
    }
}

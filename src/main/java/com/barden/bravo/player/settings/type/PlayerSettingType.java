package com.barden.bravo.player.settings.type;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Player settings.
 */
public enum PlayerSettingType {
    SPEAKER(1, boolean.class),
    SFX(1, boolean.class),
    MUSIC(1, boolean.class),
    AUTO_ACCEPT(0, boolean.class);

    private final int defaultValue;
    private final Class<?> expectedType;

    /**
     * Creates setting object.
     *
     * @param defaultValue Default value.
     * @param expectedType Expected type.
     */
    PlayerSettingType(int defaultValue, @Nonnull Class<?> expectedType) {
        this.defaultValue = defaultValue;
        this.expectedType = Objects.requireNonNull(expectedType, "expected type cannot be null!");
    }

    /**
     * Gets default value.
     *
     * @return Default value.
     */
    public int getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Gets expected type.
     *
     * @return Expected type.
     */
    @Nonnull
    public Class<?> getExpectedType() {
        return Objects.requireNonNull(this.expectedType, "expected type cannot be null!");
    }

}

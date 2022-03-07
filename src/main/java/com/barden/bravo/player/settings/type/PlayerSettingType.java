package com.barden.bravo.player.settings.type;

/**
 * Player settings.
 */
public enum PlayerSettingType {
    VFX(1d),
    MUSIC(1d),
    SKIP_WARNING_SCREEN(0d),
    AUTO_ACCEPT_MATCH(0d);

    private final double defaultValue;

    PlayerSettingType(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets default value.
     *
     * @return Default value.
     */
    public double getDefaultValue() {
        return this.defaultValue;
    }
}

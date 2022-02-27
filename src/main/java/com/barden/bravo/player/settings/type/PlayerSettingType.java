package com.barden.bravo.player.settings.type;

/**
 * Player settings.
 */
public enum PlayerSettingType {
    SPEAKER(1),
    SFX(1),
    MUSIC(1),
    AUTO_ACCEPT(0);

    private final int defaultValue;

    PlayerSettingType(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets default value.
     *
     * @return Default value.
     */
    public int getDefaultValue() {
        return this.defaultValue;
    }
}

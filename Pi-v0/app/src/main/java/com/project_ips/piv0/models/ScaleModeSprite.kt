package com.project_ips.piv0.models

/**
 * This class defines scaling properties of sprites
 */
enum class ScaleModeSprite {
    /**
     * Stretch the sprite along with the floor plan as if painted on the floor.
     */
    IMAGE,

    /**
     * Keep the size of sprite fixed even if the floor plan stretches.
     */
    SPRITE,

    /**
     * No Scaling mode. Just draw the drawable as is.
     */
    NONE
}
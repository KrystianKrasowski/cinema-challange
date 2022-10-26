package org.kkrasowski.cinema

import java.time.Duration

data class Film(val title: String,
                val duration: Duration,
                val displayType: DisplayType,
                val filmType: FilmType) {

    enum class DisplayType {
        DISPLAY_2D, DISPLAY_3D
    }

    enum class FilmType {
        BASIC, PREMIERE
    }
}

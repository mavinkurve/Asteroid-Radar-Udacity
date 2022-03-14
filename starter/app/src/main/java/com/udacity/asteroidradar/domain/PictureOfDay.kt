package com.udacity.asteroidradar.domain

import com.squareup.moshi.Json
import java.util.*

data class PictureOfDay(@Json(name = "media_type") val mediaType: String, val title: String,
                        val url: String)
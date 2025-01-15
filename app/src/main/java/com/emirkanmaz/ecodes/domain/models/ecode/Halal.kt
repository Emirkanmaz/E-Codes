package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class Halal(
    val halal: Int,
    val desc: MultiLanguageText,
    val type: MultiLanguageText
)
package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class Warning (
    val warning: Int,
    val desc: MultiLanguageText
)
package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class Risk(
    val risk: Int,
    val desc: MultiLanguageText
)
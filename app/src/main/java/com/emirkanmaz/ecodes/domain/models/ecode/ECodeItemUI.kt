package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class ECodeItemUI (
    val eCode: String,
    val halal: Halal,
    val risk: Int,
    val names: MultiLanguageText
)
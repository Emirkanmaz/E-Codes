package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class ECode(
    val ecode: String,
    val halal: Int,
    val risk: Int,
    val warning: String,
    val names: MultiLanguageText
)
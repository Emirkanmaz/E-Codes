package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class ECodeDetail (
    val eCode: ECode,
    val halal: Halal?,
    val risk: Risk?,
    val warningList: List<Warning>,
    val names: MultiLanguageText
)
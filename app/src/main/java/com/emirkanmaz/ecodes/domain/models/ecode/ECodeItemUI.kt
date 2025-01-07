package com.emirkanmaz.ecodes.domain.models.ecode

data class ECodeItemUI (
    val eCode: String,
    val halal: Halal,
    val risk: Int,
    val names: MultiLanguageText
)
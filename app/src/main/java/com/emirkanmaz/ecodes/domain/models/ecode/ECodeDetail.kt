package com.emirkanmaz.ecodes.domain.models.ecode

data class ECodeDetail (
    val eCode: ECode,
    val halal: Halal?,
    val risk: Risk?,
    val warningList: List<Warning>,
    val names: MultiLanguageText
)
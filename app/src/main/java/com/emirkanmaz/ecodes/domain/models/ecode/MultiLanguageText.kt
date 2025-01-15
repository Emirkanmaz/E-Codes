package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class MultiLanguageText(
    val tr: String,
    val en: String
) {
    fun getLocaLizedText(language: String): String{
        return when(language){
            "tr" -> tr
            "en" -> en
            else -> en
        }
    }
}
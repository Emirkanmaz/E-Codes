package com.emirkanmaz.ecodes.domain.models.ecode

import kotlinx.serialization.Serializable

@Serializable
data class MultiLanguageText(
    val tr: String,
    val en: String,
    val ru: String,
    val de: String,
    val ar: String
) {
    fun getLocaLizedText(language: String): String{
        return when(language){
            "tr" -> tr
            "en" -> en
            "ru" -> ru
            "de" -> de
            "ar" -> ar
            else -> en
        }
    }
}
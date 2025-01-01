package com.emirkanmaz.ecodes.data

import android.content.Context
import com.emirkanmaz.ecodes.domain.models.ecode.ECode
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeDetail
import com.emirkanmaz.ecodes.domain.models.ecode.Halal
import com.emirkanmaz.ecodes.domain.models.ecode.Risk
import com.emirkanmaz.ecodes.domain.models.ecode.Warning
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ECodeRepository @Inject constructor(
    private val context: Context,
    private val gson: Gson,
) {
    private val eCodes: List<ECode> by lazy { loadECodes() }
    private val halal: List<Halal> by lazy { loadHalal() }
    private val risk: List<Risk> by lazy { loadRisk() }
    private val warning: List<Warning> by lazy { loadWarning() }

    private fun loadECodes(): List<ECode> {
        return context.assets.open("ecodes.json").use { inputStream ->
            val json = inputStream.bufferedReader().use { it.readText() }
            gson.fromJson(json, Array<ECode>::class.java).toList()
        }
    }

    private fun loadHalal(): List<Halal> {
        return context.assets.open("halal.json").use { inputStream ->
            val json = inputStream.bufferedReader().use { it.readText() }
            gson.fromJson(json, Array<Halal>::class.java).toList()
        }
    }

    private fun loadRisk(): List<Risk> {
        return context.assets.open("risk.json").use { inputStream ->
            val json = inputStream.bufferedReader().use { it.readText() }
            gson.fromJson(json, Array<Risk>::class.java).toList()
        }
    }

    private fun loadWarning(): List<Warning> {
        return context.assets.open("warning.json").use { inputStream ->
            val json = inputStream.bufferedReader().use { it.readText() }
            gson.fromJson(json, Array<Warning>::class.java).toList()
        }
    }

    fun getECodeList(): List<ECode> = eCodes

    fun getECodeDetail(eCode: String): ECodeDetail? {
        val eCode = eCodes.find { it.ecode == eCode } ?: return null

        return ECodeDetail(
            eCode = eCode,
            halal = halal.find { it.halal == eCode.halal },
            risk = risk.find { it.risk == eCode.risk },
            warningList = eCode.warning.split("|").mapNotNull { warningId ->
                warning.find { it.warning == warningId.toInt() }
            },
            names = eCode.names
        )
    }

    fun searchECodes(query: String): List<ECode> {
        return eCodes.filter { eCode ->
            eCode.ecode.contains(query, ignoreCase = true) ||
            eCode.names.tr.contains(query, ignoreCase = true) ||
            eCode.names.en.contains(query, ignoreCase = true)
        }
    }
    
}
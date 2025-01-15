package com.emirkanmaz.ecodes.data

import android.content.Context
import android.util.Log
import com.emirkanmaz.ecodes.domain.models.ecode.ECode
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeDetail
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import com.emirkanmaz.ecodes.domain.models.ecode.Halal
import com.emirkanmaz.ecodes.domain.models.ecode.Risk
import com.emirkanmaz.ecodes.domain.models.ecode.Warning
import com.emirkanmaz.ecodes.utils.extensions.cleanText
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ECodeRepository @Inject constructor(
    private val context: Context,
) {

    private val eCodes: List<ECode> by lazy { loadECodes() }
    private val halal: List<Halal> by lazy { loadHalal() }
    private val risk: List<Risk> by lazy { loadRisk() }
    private val warning: List<Warning> by lazy { loadWarning() }

    private fun loadECodes(): List<ECode> {
        return try {
            context.assets.open("ecodes.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                Json.decodeFromString(json)
            }
        } catch (e: Exception) {
            Log.e("ECodeRepository", "Error parsing ecodes.json: ${e.message}", e)
            emptyList()
        }
    }

    private fun loadHalal(): List<Halal> {
        return try {
            context.assets.open("halal.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                Json.decodeFromString(json)
            }
        } catch (e: Exception) {
            Log.e("ECodeRepository", "Error parsing halal.json: ${e.message}", e)
            emptyList()
        }
    }

    private fun loadRisk(): List<Risk> {
        return try {
            context.assets.open("risk.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                Json.decodeFromString(json)
            }
        } catch (e: Exception) {
            Log.e("ECodeRepository", "Error parsing risk.json: ${e.message}", e)
            emptyList()
        }
    }

    private fun loadWarning(): List<Warning> {
        return try {
            context.assets.open("warning.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                Json.decodeFromString(json)
            }
        } catch (e: Exception) {
            Log.e("ECodeRepository", "Error parsing warning.json: ${e.message}", e)
            emptyList()
        }
    }

    fun getECodeItemUIList(): List<ECodeItemUI> {
        return eCodes.mapNotNull { eCode ->
            try {
                ECodeItemUI(
                    eCode = eCode.ecode,
                    halal = halal.find { it.halal == eCode.halal }!!,
                    risk = risk.find { it.risk == eCode.risk }!!.risk,
                    names = eCode.names
                )
            } catch (e: Exception) {
                Log.e("hata", e.toString())
                null
            }
        }
    }

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

    fun searchECode(query: String): ECodeItemUI? {
        val cleanQuery = query.cleanText()

        val foundECode = if (cleanQuery.matches(Regex("E\\d+.*", RegexOption.IGNORE_CASE))) {
            eCodes.find { it.ecode.equals(cleanQuery, ignoreCase = true) }
        } else {
            eCodes.find { eCode ->
                eCode.names.tr.equals(cleanQuery, ignoreCase = true) ||
                        eCode.names.en.equals(cleanQuery, ignoreCase = true)
            }
        }

        return foundECode?.let { eCode ->
            try {
                ECodeItemUI(
                    eCode = eCode.ecode,
                    halal = halal.find { it.halal == eCode.halal }!!,
                    risk = risk.find { it.risk == eCode.risk }!!.risk,
                    names = eCode.names
                )
            } catch (e: Exception) {
                Log.e("hata", e.toString())
                null
            }
        }
    }
    
}
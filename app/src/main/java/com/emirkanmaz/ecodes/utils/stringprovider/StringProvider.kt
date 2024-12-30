package com.emirkanmaz.ecodes.utils.stringprovider

import android.content.Context

interface StringProvider {
    fun getString(resId: Int): String
}

class DefaultStringProvider(private val context: Context) : StringProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
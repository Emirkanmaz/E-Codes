package com.emirkanmaz.ecodes.utils.photoutils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object PhotoUtils {
    fun createPhotoFile(context: Context): File {
        return File.createTempFile(
            "photo_${System.currentTimeMillis()}", ".jpg", context.cacheDir
        ).apply { deleteOnExit() }
    }

    fun getPhotoUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context, "${context.packageName}.provider", file
        )
    }
}

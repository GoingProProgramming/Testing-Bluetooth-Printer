package uk.co.goingproprogramming.tbp.extensions

import android.content.Context
import android.net.Uri
import java.io.File

fun Uri.toFile(context: Context, fileName: String): File =
    context.contentResolver.openInputStream(this)?.use { inputStream ->
        File(context.cacheDir, fileName).apply {
            this.delete()
            outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } ?: throw Exception("Uri to File failed")
package uk.co.goingproprogramming.tbp.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun Int.saveDrawableAsBitmapOnFile(context: Context): File {
    val bitmap = getBitmapFromImage(context, this)
    val file = File(context.cacheDir, "image.png")
    if (file.exists())
        file.delete()
    file.outputStream().use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
    }
    return file
}

private fun getBitmapFromImage(context: Context, drawable: Int): Bitmap =
    BitmapFactory.decodeResource(context.resources, drawable)
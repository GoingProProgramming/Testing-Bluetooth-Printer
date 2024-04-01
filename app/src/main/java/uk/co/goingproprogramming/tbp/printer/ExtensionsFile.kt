package uk.co.goingproprogramming.tbp.printer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

fun File.toBitmap(): Bitmap =
    BitmapFactory.decodeFile(absolutePath)
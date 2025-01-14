package com.example.stylishadmin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun getResizedAndCompressedBitmap(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
        // Calculate aspect ratio and scale the image
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
        val newWidth: Int
        val newHeight: Int

        if (aspectRatio > 1) { // Landscape
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else { // Portrait
            newWidth = (maxHeight * aspectRatio).toInt()
            newHeight = maxHeight
        }

        // Resize the bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        // Compress the bitmap to a JPEG with specified quality
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        return byteArrayOutputStream.toByteArray()
    }
    fun decodeUriToBitmap(context: Context, imageUri: Uri): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        return BitmapFactory.decodeStream(inputStream)
    }
    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width = if (bitmap.width > bitmap.height) maxWidth else (maxHeight * aspectRatio).toInt()
        val height = if (bitmap.height > bitmap.width) maxHeight else (maxWidth / aspectRatio).toInt()

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    fun compressBitmap(bitmap: Bitmap, quality: Int = 80): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
    fun getFileSizeFromUri(context: Context, imageUri: Uri): Long {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        return inputStream?.available()?.toLong() ?: 0L
    }
    fun shouldCompressImage(imageUri: Uri, context: Context, maxSize: Long): Boolean {
        val fileSize = getFileSizeFromUri(context, imageUri)
        return fileSize > maxSize
    }

}

package com.example.prashantadvaitdemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object ImageLoader {

    private val handler = android.os.Handler(Looper.getMainLooper())
    fun loadImage(url: String?, onSuccess: (Bitmap) -> Unit, onFailure: (String) -> Unit) {
        if (url == null) {
            onFailure("Invalid URL")
            return
        }

        val cachedBitmap = ImageCache.getBitmapFromMemCache(url)
        if (cachedBitmap != null) {
            handler.post { onSuccess(cachedBitmap) }
            return
        }

        thread(start = true) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                connection.disconnect()
                input.close()

                if (bitmap == null) {
                    onFailure("Failed to load image")
                    return@thread
                } else {
                    ImageCache.addBitmapToMemoryCache(url, bitmap)
                    handler.post {
                        onSuccess(bitmap)
                    }
                }
            } catch (e: Exception) {
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
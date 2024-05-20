package com.example.countries_info_app.api

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpClientSingleton {
    private const val CACHE_SIZE_BYTES = (10 * 1024 * 1024) // 10 MB

    private lateinit var cache: Cache
    private var instance: OkHttpClient? = null

    fun getInstance(context: Context): OkHttpClient {
        if (instance == null) {
            cache = Cache(directory = context.cacheDir, maxSize = CACHE_SIZE_BYTES.toLong())

            instance = OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        return instance!!
    }
}
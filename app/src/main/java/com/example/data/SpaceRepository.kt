package com.example.data

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class ApiBodyResponse(
    val id: String,
    val name: String,
    val englishName: String?,
    val semimajorAxis: Double?,
    val perihelion: Double?,
    val aphelion: Double?,
    val eccentricity: Double?,
    val inclination: Double?,
    val density: Double?,
    val gravity: Double?,
    val escape: Double?,
    val moons: List<Any>?,
    val discoveredBy: String?,
    val discoveryDate: String?,
    val axialTilt: Double?,
    val avgTemp: Double?,
    val meanRadius: Double?
)

interface SpaceApiService {
    @GET("v1/bodies/{id}")
    suspend fun getBodyData(@Path("id") id: String): ApiBodyResponse
}

sealed interface ApiSyncState {
    object Latent : ApiSyncState
    object Syncing : ApiSyncState
    data class Synced(val apiSource: String, val lastUpdated: Long) : ApiSyncState
    data class Failed(val error: String) : ApiSyncState
}

class SpaceRepository {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.le-systeme-solaire.net/rest/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService = retrofit.create(SpaceApiService::class.java)

    suspend fun fetchLiveCelestialBody(id: String): ApiBodyResponse? {
        return try {
            Log.d("SpaceRepository", "Fetching live astronomy data for: $id")
            // Map common planet names to matching openData ids
            val apiId = when (id.lowercase()) {
                "earth" -> "terre"
                else -> id.lowercase()
            }
            apiService.getBodyData(apiId)
        } catch (e: Exception) {
            Log.e("SpaceRepository", "Failed to contact open space API for $id, falling back. Error: ${e.message}")
            null
        }
    }
}

package com.mf.weatherapp.data.repositories.network


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {
    private const val BASE_URL = "https://api.openweathermap.org/"

    @Provides
    @Singleton
    fun provideRetrofit(): ApiService {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // use GSon
//            .addConverterFactory(json.asConverterFactory(contentType)) // Use Kotlin Converter for JSON conversion
            .build().create(ApiService::class.java)

    }
//    val api: ApiService by lazy {
//        val json = Json { ignoreUnknownKeys = true }
//        val contentType = "application/json".toMediaType()
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(json.asConverterFactory(contentType))
//            .build()
//            .create(ApiService::class.java)
//    }
}

//class RateLimitInterceptor(private val timeFrameMillis: Long, private val maxRequests: Int) :
//    Interceptor {
//    private var requestCount = 0
//    private var startTime = System.currentTimeMillis()
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val currentTime = System.currentTimeMillis()
//
//        // Reset the request count if the time frame has passed
//        if (currentTime - startTime > timeFrameMillis) {
//            startTime = currentTime
//            requestCount = 0
//        }
//
//        // Check if request limit has been reached
//        if (requestCount >= maxRequests) {
//            val waitTime = timeFrameMillis - (currentTime - startTime)
//            if (waitTime > 0) {
//                // Delay the request until the time frame is reset
//                Thread.sleep(waitTime)
//            }
//            // Reset after the delay
//            startTime = System.currentTimeMillis()
//            requestCount = 0
//        }
//
//        requestCount++
//        return chain.proceed(chain.request())
//    }
//}
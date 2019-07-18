package ru.karapetiandav.hearthstonecards.services

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.models.CardDeserializer
import ru.karapetiandav.hearthstonecards.network.CardsApi
import java.util.concurrent.TimeUnit

class ApiService(private val connectivityService: ConnectivityService, private val context: Context) {

    private val BASE_URL = "https://omgvamp-hearthstone-v1.p.rapidapi.com/"
    private val retrofit: Retrofit

    init {
        val httpClient = buildHttpClient()
        val gson = GsonBuilder().registerTypeAdapter(Card::class.java, CardDeserializer()).create()

        retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun buildHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cacheInterceptor =
            CacheInterceptor(connectivityService)

        return OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, 10 * 1024 * 1024)) // 10 MB
            .addInterceptor(loggingInterceptor)
            .addInterceptor(cacheInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .readTimeout(15L, TimeUnit.SECONDS)
            .build()
    }

    fun getCardsApi(): CardsApi = retrofit.create(CardsApi::class.java)

    private class CacheInterceptor(private val connectivityService: ConnectivityService): Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val cacheHeaderValue = if (connectivityService.hasNetwork())
                "public, max-age=2419200"
            else
                "public, only-if-cached, max-stale=2419200"
            val request = originalRequest.newBuilder().build()
            val response = chain.proceed(request)
            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheHeaderValue)
                .build()
        }
    }
}
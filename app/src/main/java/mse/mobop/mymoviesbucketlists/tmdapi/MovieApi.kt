package mse.mobop.mymoviesbucketlists.tmdapi

import mse.mobop.mymoviesbucketlists.utils.BASE_URL_API
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object MovieApi {
    private var retrofit: Retrofit? = null

    private fun buildClient(): OkHttpClient? {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .client(buildClient()!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL_API)
                    .build()
            }
            return retrofit
        }
}
package it.icemangp.fastapp.ui.main

import com.squareup.moshi.Moshi
import it.icemangp.shakenetworklog.data.NetworkLogInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


object MainRepository {

    data class Repo(val id: String)

    interface GitHubService {
        @GET("users/{user}/repos")
        fun repoList(@Path("user") user: String?): Call<List<Repo>>
    }

    val retrofit: Retrofit
    val service: GitHubService


    init {
        val connectionTimeout = 30L
        val readTimeout = 30L

        val networkLogInterceptor = NetworkLogInterceptor()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(networkLogInterceptor)
            .connectTimeout(connectionTimeout, TimeUnit.SECONDS) // connect timeout
            .readTimeout(readTimeout, TimeUnit.SECONDS)    // socket timeout
            .build()

        val moshiBuilder = Moshi.Builder().build()

        val converterFactory = MoshiConverterFactory.create(moshiBuilder)//.withNullSerialization()

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/"/*BuildConfig.API_BASE_URL*/)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()

        service = retrofit.create(GitHubService::class.java)
    }

    fun sampleNetworkCall(user: String): Response<List<Repo>>? {
        return try {
            service.repoList(user).execute()
        }
        catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
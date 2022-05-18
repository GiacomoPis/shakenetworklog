package it.icemangp.shakenetworklog.data

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Level

 class NetworkLogInterceptor : Interceptor {

    var level: Level = Level.ALL

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        if (level == Level.OFF) {
            return chain.proceed(request)
        }

        val response: Response
        val start = System.nanoTime()
        try {
            response = chain.proceed(request)
            NetworkLogManager.addCall(request, response, durationInMillis(start))
        } catch (e: IOException) {
            NetworkLogManager.addCall(request, e, durationInMillis(start))
            throw e
        }

        return response
    }

     private fun durationInMillis(start: Long): Long {
         return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
     }

}

package it.icemangp.shakenetworklog.data

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import it.icemangp.shakenetworklog.ui.ActivityNetworkCallList
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.StatusLine
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.lang.Exception
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayDeque


object NetworkLogManager {

    val data = ArrayDeque<NetworkCall>(50)

    private lateinit var application: Context
    private var sensorManager: SensorManager? = null

    var shakeEnabled = true

    private var lifecycle: Lifecycle? = null

    fun init(appContext: Context) {
        this.application = appContext

        val shakeListener = object : ShakeListener() {
            override fun onShake() {
                Log.d("TAG", "currentState: ${lifecycle?.currentState}")
                if (shakeEnabled && lifecycle?.currentState == Lifecycle.State.RESUMED)
                    openLog()
            }
        }

        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        sensorManager?.registerListener(shakeListener, sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun openLog() {
        val intent = Intent(application, ActivityNetworkCallList::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    fun start(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
    }

    // ********************************************************************************

    fun addCall(request: Request, response: Response, durationInMillis: Long) {
        val networkCallWithRequestData = buildNetworkCallFromRequest(request, durationInMillis)
        val networkCallWithResponseData = addResponseToNetworkCall(networkCallWithRequestData, response)
        data.addFirst(networkCallWithResponseData)
    }

    fun addCall(request: Request, exception: Exception, durationInMillis: Long) {
        val networkCallWithRequestData = buildNetworkCallFromRequest(request, durationInMillis)
        val networkCallWithException = networkCallWithRequestData.copy(
            exceptionMessage = exception.localizedMessage ?: ""
        )
        data.addFirst(networkCallWithException)
    }

    fun findCallWithId(callId: String): NetworkCall? {
        return data.find { it.id == callId }
    }

    fun clear() {
        data.clear()
    }

    private fun parseDateFromHeader(utcDate: String): Date {
        try {
            val formatterParser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
            return formatterParser.parse(utcDate) ?: throw Exception("Failed to parse date $utcDate")
        } catch (e: Exception) {
            e.printStackTrace()
            return Date()
        }
    }

    private fun addResponseToNetworkCall(call: NetworkCall, response: Response): NetworkCall {
        val requestCode = response.code
        val responseHeaders = headersFrom(response)
        val responseBody = bodyFrom(response)
        val dateTime = parseDateFromHeader(responseHeaders["date"] ?: "")

        return call.copy(
            responseCode = requestCode,
            responseHeaders = responseHeaders,
            responseBody = responseBody,
            responseHeaderDate = dateTime,
        )
    }

    private fun buildNetworkCallFromRequest(request: Request, durationInMillis: Long): NetworkCall {
        return NetworkCall(
            id = UUID.randomUUID().toString(),
            method = request.method,
            url = request.url.toString(),
            duration = "$durationInMillis ms",
            requestHeaders = headersFrom(request),
            requestBody = bodyFrom(request),
            responseHeaders = mapOf(),
            responseBody = "",
            exceptionMessage = "",
            requestDate = Date(),
            responseHeaderDate = Date(),
        )
    }

    private fun bodyFrom(request: Request): String {
        val body = request.body

        return when {
            body == null -> ""
            body.isOneShot() || body.isDuplex() -> "oneshot or duplex body omitted"
            bodyHasUnknownEncoding(request.headers) -> "encoded body omitted"

            else -> {
                val builder = StringBuilder()
                val buffer = Buffer()
                body.writeTo(buffer)
                return try {
                    if (buffer.isProbablyUtf8()) {
                        builder.appendLine(buffer.readUtf8())
                        builder.toString()
                    } else {
                        "Binary ${body.contentLength()}-byte body omitted)"
                    }
                } catch (e: Exception) {
                    return "Exception parsing body: ${e.localizedMessage}"
                }
            }
        }
    }

    private fun bodyFrom(response: Response): String {

        val headers = response.headers
        val body = response.body
        return when {
            body == null || response.promisesBody().not() -> "none"
            bodyHasUnknownEncoding(response.headers) -> "encoded body omitted"
            else -> {
                val builder = StringBuilder()
                val source = response.body?.source()
                source?.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source?.buffer

                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    GzipSource(buffer?.clone()!!).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer?.writeAll(gzippedResponseBody)
                    }
                }

                if (buffer?.isProbablyUtf8() != true) {
                    return "binary ${buffer?.size}-byte body omitted"
                }

                if (body.contentLength() != 0L) {
                    builder.appendLine(buffer?.clone()?.readUtf8())
                }

                return builder.toString()
            }
        }
    }

    // *********************************************************************+

    private fun headersFrom(response: Response): Map<String, String> {
        return response.headers.toMap()
    }

    private fun headersFrom(request: Request): Map<String, String> {

        val headersMap = mutableMapOf<String, String>()

        val requestBody = request.body
        val requestHeaders = request.headers

        if (requestBody != null) {
            // Request body headers are only present when installed as a network interceptor. When not
            // already present, force them to be included (if available) so their values are known.
            requestBody.contentType()?.let {
                if (requestHeaders["Content-Type"] == null) {
                    headersMap["Content-Type"] = it.toString()
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (requestHeaders["Content-Length"] == null) {
                    headersMap["Content-Length"] = requestBody.contentLength().toString()
                }
            }
        }

        headersMap.putAll(request.headers.toMap())

        return headersMap
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }

    private fun Response.promisesBody(): Boolean {
        // HEAD requests never yield a body regardless of the response headers.
        if (request.method == "HEAD") {
            return false
        }

        val responseCode = code
        if ((responseCode < StatusLine.HTTP_CONTINUE || responseCode >= 200) &&
            responseCode != HttpURLConnection.HTTP_NO_CONTENT &&
            responseCode != HttpURLConnection.HTTP_NOT_MODIFIED
        ) {
            return true
        }

        // If the Content-Length or Transfer-Encoding headers disagree with the response code, the
        // response is malformed. For best compatibility, we honor the headers.
        if (headersContentLength() != -1L ||
            "chunked".equals(header("Transfer-Encoding"), ignoreCase = true)
        ) {
            return true
        }
        return false
    }

    private fun Response.headersContentLength(): Long {
        return (headers["Content-Length"]?.toLongOrNull()) ?: -1L
    }
}

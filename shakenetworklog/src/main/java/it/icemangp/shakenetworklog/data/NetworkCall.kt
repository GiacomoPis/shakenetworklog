package it.icemangp.shakenetworklog.data

data class NetworkCall(
    val id: String,
    val method: String,
    val url: String,
    val duration: String,
    val responseCode: Int? = null,
    val requestHeaders: Map<String, String>,
    val requestBody: String,
    val responseHeaders: Map<String,String>,
    val responseBody: String,
    val exceptionMessage: String
)

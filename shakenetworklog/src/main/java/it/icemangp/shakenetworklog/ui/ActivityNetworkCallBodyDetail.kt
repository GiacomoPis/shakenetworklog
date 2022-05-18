package it.icemangp.shakenetworklog.ui

import android.os.Bundle
import android.widget.TextView
import it.icemangp.shakenetworklog.R
import it.icemangp.shakenetworklog.data.NetworkLogManager
import org.json.JSONArray
import org.json.JSONObject


class ActivityNetworkCallBodyDetail : ActivityBaseNetworkLog() {

    companion object {
        const val NETWORK_CALL_ID = "ActivityNetworkCallDetail_NETWORK_CALL_ID"
        const val BODY_TYPE = "ActivityNetworkCallDetail_BODY_TYPE"
        const val REQUEST = "ActivityNetworkCallDetail_REQUEST"
        const val RESPONSE = "ActivityNetworkCallDetail_RESPONSE"
    }

    lateinit var body: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_call_body_detail)

        val networkCallId = intent.getStringExtra(NETWORK_CALL_ID) ?: throw IllegalArgumentException("")
        val bodyType = intent.getStringExtra(BODY_TYPE) ?: throw IllegalArgumentException("")
        val networkCall = NetworkLogManager.findCallWithId(networkCallId)

        body = when(bodyType) {
            REQUEST -> networkCall?.requestBody ?: ""
            RESPONSE -> networkCall?.responseBody ?: ""
            else -> ""
        }

        initTextView()
    }

    private fun initTextView() {
        val textView = findViewById<TextView>(R.id.networkCallBodyDetailContent)

        val jsonFailed = tryFormattingJsonObject(textView).not()

        if (jsonFailed) {
            val jsonArrayFailed = tryFormattingJsonArray(textView).not()
            if (jsonArrayFailed) {
                textView.text = body
            }
        }
    }

    private fun tryFormattingJsonObject(textView: TextView): Boolean {
        return try {
            val json = JSONObject(body)
            val formatted: String = json.toString(4)
            textView.text = formatted
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun tryFormattingJsonArray(textView: TextView): Boolean {
        return try {
            val jsonArray = JSONArray(body)
            val formatted: String = jsonArray.toString(4)
            textView.text = formatted
            true
        } catch (e: Exception) {
            false
        }
    }
}
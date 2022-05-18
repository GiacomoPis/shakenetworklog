package it.icemangp.shakenetworklog.ui

import android.content.Intent
import android.os.Bundle
import android.text.Spanned
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.google.android.material.button.MaterialButton
import it.icemangp.shakenetworklog.R
import it.icemangp.shakenetworklog.data.NetworkCall
import it.icemangp.shakenetworklog.data.NetworkLogManager
import java.lang.Exception
import java.net.URI

class ActivityNetworkCallDetail : ActivityBaseNetworkLog() {

    private var networkCall: NetworkCall? = null

    companion object {
        const val NETWORK_CALL_ID = "ActivityNetworkCallDetail_NETWORK_CALL_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_call_detail)

        val networkCallId = intent.getStringExtra(NETWORK_CALL_ID) ?: throw IllegalArgumentException("NETWORK_CALL_ID cannot be null")
        networkCall = NetworkLogManager.findCallWithId(networkCallId)

        initUi()
    }

    private fun initUi() {
        initPath()
        initOverview()
        initRequestHeaders()
        initResponseHeaders()
        initButtons()
    }

    private fun initButtons() {
        val requestBodyButton  = findViewById<MaterialButton>(R.id.requestBodyButton)
        val responseBodyButton = findViewById<MaterialButton>(R.id.responseBodyButton)

        requestBodyButton.setOnClickListener {
            openBodyActivity(ActivityNetworkCallBodyDetail.REQUEST)
        }
        responseBodyButton.setOnClickListener {
            openBodyActivity(ActivityNetworkCallBodyDetail.RESPONSE)
        }
    }

    private fun openBodyActivity(type: String) {
        val intent = Intent(this, ActivityNetworkCallBodyDetail::class.java)
        intent.putExtra(ActivityNetworkCallBodyDetail.NETWORK_CALL_ID, networkCall?.id)
        intent.putExtra(ActivityNetworkCallBodyDetail.BODY_TYPE, type)
        startActivity(intent)
    }

    private fun initRequestHeaders() {
        val requestHeaderContent = findViewById<TextView>(R.id.networkCallDetailRequestHeaderContent)
        requestHeaderContent.text = toHtmlString(networkCall?.requestHeaders ?: mapOf())
    }

    private fun initResponseHeaders() {
        val responseHeaderContent = findViewById<TextView>(R.id.networkCallDetailResponseHeaderContent)
        responseHeaderContent.text = toHtmlString(networkCall?.responseHeaders ?: mapOf())
    }

    private fun initOverview() {
        val requestOverviewContent = findViewById<TextView>(R.id.networkCallDetailOverviewContent)
        requestOverviewContent.text = toHtmlString(
            mapOf(
                "URL"           to (networkCall?.url ?: ""),
                "Method"        to (networkCall?.method ?: ""),
                "Response Code" to (networkCall?.responseCode?.toString() ?: ""),
                "Duration"      to (networkCall?.duration ?: "")
            )
        )
    }

    private fun initPath() {
        val url = try {
            URI(networkCall?.url)
        } catch (e: Exception) {
            null
        }
        val networkCallPath = findViewById<TextView>(R.id.networkCallDetailPath)
        networkCallPath.text = url?.path
    }


    private fun toHtmlString(map: Map<String, String>): Spanned {
        val builder = StringBuilder()

        map.onEachIndexed { index, entry ->

            builder.append("<b>${entry.key}</b>: ${entry.value}")
            if (index != map.size - 1) builder.append("<br>")
        }
        return HtmlCompat.fromHtml(
            builder.toString(),
            HtmlCompat.FROM_HTML_MODE_COMPACT)
    }


}
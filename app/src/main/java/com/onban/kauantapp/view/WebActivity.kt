package com.onban.kauantapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.onban.kauantapp.R

class WebActivity : AppCompatActivity() {

    private lateinit var mWebSettings: WebSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        webViewSetting()

        val url = intent.getStringExtra("url")
        url?.let {
            findViewById<WebView>(R.id.webView).loadUrl(it)
        }
    }

    private fun webViewSetting() {
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient= WebViewClient()
        mWebSettings = webView.settings
        true.also { mWebSettings.javaScriptEnabled = it }
        mWebSettings.setSupportMultipleWindows(false) // 새창 띄우기 허용 여부
        mWebSettings.useWideViewPort= true // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false) // 화면 줌 허용 여부
        mWebSettings.builtInZoomControls= false // 화면 확대 축소 허용 여부
    }
}
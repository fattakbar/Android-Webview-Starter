package com.adyoi.webviewstarter


import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.webkit.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onBackPressed() {
        val myWebView: WebView = findViewById(R.id.webview)
        if (myWebView.canGoBack()) {
            myWebView.goBack()
        } else {
            AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    finish()
                })
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Webview Starter v 1.1
        // Created by Adi Apriyanto
        // Tangerang Selatan, March 2018
        // Permission to copy source code is only permitted for Education

        // Sample Resource Page
        val myUrl = "file:///android_asset/html/index.html"

        // Your Live Page
        // val myUrl = "https://www.domain.com"
        val myWebView: WebView = findViewById(R.id.webview)

        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true
        myWebView.settings.allowContentAccess = true
        myWebView.addJavascriptInterface(
            WebInterface(this, myWebView),
            "Android"
        )
        myWebView.webChromeClient = WebChromeClient()
        myWebView.loadUrl(myUrl)
        myWebView.reload()
    }

    private inner class WebInterface(val mContext: Context, val mView: WebView) {
        // From Webview to Android
        @JavascriptInterface
        fun showToast(Text: String) {
            Toast.makeText((applicationContext), Text, Toast.LENGTH_SHORT).show()
        }
        @JavascriptInterface
        fun showNotification(id: Int, title: String, text: String) {
            createNotificationChannel(id, title, text)
        }

        // From Android to Webview
        @JavascriptInterface
        fun sendText(Text: String) {
            val myWebView: WebView = findViewById(R.id.webview)
            var stamp = "Hello :) - "
            myWebView.post{myWebView.loadUrl("javascript:showText(\"$stamp$Text\")")}
        }
    }

    private fun createNotificationChannel(notificationId: Int, notificationTitle: String, notificationText: String) {
        val channel_id = "ANDROID_WEBVIEW_STARTER"
        val channel_name = "Notification Default"
        val channel_description = ""
        val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channel_id, channel_name, importance)
                channel.description = channel_description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        val notificationManager = NotificationManagerCompat.from(this)
        val builder = NotificationCompat
            .Builder(this, channel_id)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            notificationManager.notify(notificationId, builder.build())
    }
}

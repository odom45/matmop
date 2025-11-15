package com.matmop.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class AgreementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)

        val webView: WebView = findViewById(R.id.agreementWebView)
        val checkAgree: CheckBox = findViewById(R.id.checkboxAgree)
        val btnAccept: Button = findViewById(R.id.btnAccept)
        val btnDecline: Button = findViewById(R.id.btnDecline)

        webView.settings.javaScriptEnabled = false

        // Load the agreement markdown and render as simple HTML
        val md = assets.open("pages/01_user_agreement.md").bufferedReader().use { it.readText() }
        val parser = Parser.builder().build()
        val document = parser.parse(md)
        val renderer = HtmlRenderer.builder().build()
        var html = renderer.render(document)
        html = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>body{font-family:sans-serif;padding:16px;line-height:1.5} h1,h2,h3{margin-top:1em}</style></head><body>" + html + "</body></html>"
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        btnAccept.setOnClickListener { _: View ->
            if (!checkAgree.isChecked) {
                Toast.makeText(this, getString(R.string.agree_required_to_continue), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getSharedPreferences("matmop_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("eula_accepted", true)
                .apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        btnDecline.setOnClickListener {
            // Exit the app if the user declines
            finishAffinity()
        }
    }
}

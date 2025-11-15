package com.matmop.app

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MainActivity : AppCompatActivity() {

    private lateinit var pagesList: ListView
    private lateinit var contentWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pagesList = findViewById(R.id.pagesList)
        contentWebView = findViewById(R.id.contentWebView)
        contentWebView.settings.javaScriptEnabled = false

        val btnGallery: Button = findViewById(R.id.btnGallery)
        btnGallery.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        val pageFiles = assets.list("pages")?.sorted() ?: arrayOf()
        val titles = pageFiles.map { it.replace(".md", "").replace('_', ' ').replace("page ", "Page ") }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)
        pagesList.adapter = adapter

        pagesList.setOnItemClickListener { _, _, position, _ ->
            val fileName = pageFiles[position]
            val md = assets.open("pages/$fileName").bufferedReader().use { it.readText() }
            val parser = Parser.builder().build()
            val document = parser.parse(md)
            val renderer = HtmlRenderer.builder().build()
            var html = renderer.render(document)

            // Add a tiny footer from native layer
            val nativeInfo = NativeLib.stringFromJNI()
            html = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>body{font-family: sans-serif; padding:16px;}</style></head><body>" + html + "<hr/><small>" + nativeInfo + "</small></body></html>"

            contentWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        }

        // Auto-open first page if present
        if (pageFiles.isNotEmpty()) {
            pagesList.performItemClick(pagesList, 0, pagesList.adapter.getItemId(0))
        }
    }
}

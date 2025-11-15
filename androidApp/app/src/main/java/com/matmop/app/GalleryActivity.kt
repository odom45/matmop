package com.matmop.app

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

data class GalleryItem(val name: String, val type: String)

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val listView: ListView = findViewById(R.id.galleryList)
        val items = mutableListOf<GalleryItem>()

        // list images
        try {
            val images = assets.list("images") ?: arrayOf()
            for (img in images) items.add(GalleryItem(img, "image"))
        } catch (e: Exception) { /* ignore missing folder */ }

        // list pdfs
        try {
            val pdfs = assets.list("pdfs") ?: arrayOf()
            for (pdf in pdfs) items.add(GalleryItem(pdf, "pdf"))
        } catch (e: Exception) { /* ignore missing folder */ }

        if (items.isEmpty()) {
            items.add(GalleryItem("No gallery assets found. Copy images to assets/images and PDFs to assets/pdfs.", "info"))
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items.map { "[${it.type}] ${it.name}" })
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = items[position]
            if (item.type == "image") {
                val intent = Intent(this, ImageViewerActivity::class.java)
                intent.putExtra("path", "images/${item.name}")
                startActivity(intent)
            } else if (item.type == "pdf") {
                val intent = Intent(this, PdfViewerActivity::class.java)
                intent.putExtra("path", "pdfs/${item.name}")
                startActivity(intent)
            }
        }
    }
}


package com.matmop.app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val imageView: ImageView = findViewById(R.id.imageView)
        val path = intent.getStringExtra("path")
        if (path == null) return

        try {
            assets.open(path).use { input ->
                val bitmap = BitmapFactory.decodeStream(input)
                imageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


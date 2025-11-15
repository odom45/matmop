package com.matmop.app

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PdfViewerActivity"

class PdfViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        val imageView: ImageView = findViewById(R.id.imageView)
        val path = intent.getStringExtra("path")
        if (path.isNullOrBlank()) {
            Log.e(TAG, "Missing PDF path in intent extras")
            return
        }

        try {
            // Copy asset to a cache file because PdfRenderer needs a FileDescriptor
            val fileName = path.substringAfterLast('/')
            val outFile = File(cacheDir, fileName)
            assets.open(path).use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }

            val parcelFileDescriptor: ParcelFileDescriptor = ParcelFileDescriptor.open(outFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)

            if (pdfRenderer.pageCount > 0) {
                val page = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                imageView.setImageBitmap(bitmap)
                page.close()
            } else {
                Log.w(TAG, "PDF has no pages: $fileName")
            }

            pdfRenderer.close()
            parcelFileDescriptor.close()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to render PDF: $path", e)
        }
    }
}


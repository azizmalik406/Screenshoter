package com.example.screenshotapp

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nmd.screenshot.Screenshot
import android.content.Intent
import java.lang.Exception
import android.provider.MediaStore.Images

import android.content.ContentValues
import android.net.Uri
import java.io.OutputStream


class MainActivity : AppCompatActivity() {
    lateinit var take_screenshot:TextView
    private var screenshot: Screenshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        screenshot = Screenshot(this)
        take_screenshot=findViewById(R.id.take_screenshot)
        take_screenshot.setOnClickListener {
            take()
        }
    }
    fun take() {
        screenshot?.notificationTitle("My screenshot title")
        screenshot?.setCallback(object : Screenshot.OnResultListener {
            override fun result(success: Boolean, filePath: String?, bitmap: Bitmap?) {
                bitmap?.let { share(it) }
                Log.d("bitmap","$bitmap")
             }
        })
        screenshot?.takeScreenshot()
    }

    fun share(bitmap: Bitmap){
        val icon: Bitmap = bitmap
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"

        val values = ContentValues()
        values.put(Images.Media.TITLE, "title")
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        val uri: Uri? = contentResolver.insert(
            Images.Media.EXTERNAL_CONTENT_URI,
            values
        )


        val outstream: OutputStream?
        try {
            outstream = uri?.let { contentResolver.openOutputStream(it) }
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream)
            outstream?.close()
        } catch (e: Exception) {
            System.err.println(e.toString())
        }

        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Share Image"))
    }
}
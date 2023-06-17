package com.example.wallpapers

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wallpapers.databinding.ActivityPreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPreviewBinding
    private var imageUrl : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUrl = intent.getStringExtra("url")

        Glide.with(this).load(imageUrl).into(binding.image)

        binding.setWallpaper.setOnClickListener {
            binding.pgBar.isIndeterminate = true
            binding.pgBackground.visibility = View.VISIBLE


            setDeviceWallpaper()


        }

        binding.save.setOnClickListener{
            binding.pgBar.isIndeterminate = true
            binding.pgBackground.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                val inputStream = URL(imageUrl).openStream()
                val file = createImageFile()
                val outputStream = BufferedOutputStream(FileOutputStream(file))

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                saveImageToMediaStore(file)
                withContext(Dispatchers.Main){
                    binding.pgBar.isIndeterminate = false
                    binding.pgBackground.visibility = View.GONE
                    Toast.makeText(this@PreviewActivity,"downloaded",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setDeviceWallpaper() {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = (binding.image.drawable as BitmapDrawable).bitmap
            val manager = WallpaperManager.getInstance(applicationContext)
            manager.setBitmap(bitmap)

            withContext(Dispatchers.Main){
                binding.pgBar.isIndeterminate = false
                binding.pgBackground.visibility = View.GONE
                Toast.makeText(this@PreviewActivity,"set successfully",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().toString()
        val imageName = "image_$timeStamp.jpg"
        val storageDir = getExternalStoragePublicDirectoryCompat(Environment.DIRECTORY_PICTURES)

        return File(storageDir, imageName)
    }

    private fun getExternalStoragePublicDirectoryCompat(type: String): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.getExternalStoragePublicDirectory(type)
        } else {
            @Suppress("DEPRECATION")
            Environment.getExternalStoragePublicDirectory(type)
        }
    }

    private fun saveImageToMediaStore(file: File) {
        val mimeType = getMimeType(file.absolutePath)
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val contentResolver: ContentResolver = applicationContext.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { returnUri ->
            contentResolver.openOutputStream(returnUri)?.use { outputStream ->
                BufferedInputStream(file.inputStream()).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

    }

    private fun getMimeType(url: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }


}
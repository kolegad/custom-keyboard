package studio.codable.customkeyboard.data

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.FileProvider
import okhttp3.*
import studio.codable.customkeyboard.BuildConfig
import studio.codable.customkeyboard.model.DownloadStatus
import java.io.File
import java.io.IOException

object DataManager {

    private const val ANIMALS_FOLDER = "animals"
    private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider"
    private const val TAG = "CUSTOM_KEYBOARD"
    private const val MAX_FILE_NUMBER_IN_CACHE_DIR = 20

    fun shareAnimal(
        context: Context,
        animalImageUrl: String,
        animalName: String,
        onResponse: (DownloadStatus) -> Unit
    ) {
        downloadFile(
            context,
            url = animalImageUrl,
            fileName = animalName
        ) {
            Handler(Looper.getMainLooper()).post {
                onResponse(it)
            }
        }
    }

    /**
     * Returns true if the file specified by the [url] already exists in our application cache,
     * else false.
     */
    private fun downloadFile(
        context: Context,
        url: String,
        fileName: String,
        onResponse: (DownloadStatus) -> Unit
    ): Boolean {
        val mediaStorageDir = File(
            context.cacheDir,
            ANIMALS_FOLDER
        )

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Folder animals/ exists")
            }
        }

        val file = File(mediaStorageDir, "$fileName.jpg")

        if (file.exists()) {
            onResponse(DownloadStatus.Success.FromCache(file))
            return true
        } else {
            val downloadingFile = File(mediaStorageDir, "$fileName-downloading.jpg")

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val client = OkHttpClient()

            Log.d(TAG, "Starting download")
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Download successful.")
                        try {
                            response.body?.byteStream()
                                ?.copyTo(downloadingFile.outputStream())
                            downloadingFile.renameTo(file)
                            onResponse(DownloadStatus.Success.Downloaded(file))
                            mediaStorageDir.listFiles()?.let {
                                if (it.size > MAX_FILE_NUMBER_IN_CACHE_DIR) removeLastFile(
                                    context
                                )
                            }
                        } catch (t: Throwable) {
                            onResponse(DownloadStatus.Fail.IOError(t))
                        }
                    } else {
                        Log.w(TAG, "Download failed.")
                        onResponse(DownloadStatus.Fail.NetworkError)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.w(TAG, "onFailureDownload ${e.cause.toString()}")
                    onResponse(DownloadStatus.Fail.IOError(e.cause))
                }
            })
        }
        return false
    }

    private fun removeLastFile(context: Context) {
        val allFiles = File(
            context.cacheDir,
            ANIMALS_FOLDER
        ).listFiles()

        if (!allFiles.isNullOrEmpty()) {
            val toBeDeleted = allFiles.minByOrNull { it.lastModified() }
            toBeDeleted?.delete()
            Log.d(TAG, "File $toBeDeleted deleted")
        }
    }

    fun getKeyboardSharingIntent(
        context: Context,
        destPackageName: String,
        file: File,
        type: String = "image/jpeg"
    ): Intent {
        val uri = FileProvider.getUriForFile(context, AUTHORITY, file)

        val mimeType = context.contentResolver.getType(uri)
        Log.d(TAG, "Mime type $mimeType")

        return Intent(Intent.ACTION_SEND)
            .setType(type)
            .putExtra(Intent.EXTRA_STREAM, uri)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setPackage(destPackageName)
    }

}
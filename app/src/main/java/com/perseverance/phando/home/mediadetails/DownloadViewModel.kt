package com.perseverance.phando.home.mediadetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadViewModel : ViewModel() {
    private val downloadProgressBar = MutableLiveData<Int>()
    val downloadProgress: LiveData<Int> get() = downloadProgressBar

    fun downloadFile(url: String, destinationFile: File) {
        viewModelScope.launch {
            try {
                val inputStream = URL(url).openStream()
                val outputStream = FileOutputStream(destinationFile)
                val buffer = ByteArray(1024)
                var bytesRead: Int
                var totalBytesRead = 0L
                val totalFileSize = getContentLength(url) // Function to get the total file size

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    totalBytesRead += bytesRead
                    val progress = ((totalBytesRead * 100) / totalFileSize).toInt()
                    downloadProgressBar.postValue(progress) // Update the progress
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()

            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }



    private fun getContentLength(url: String): Long {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.connect()
        return connection.contentLength.toLong()
    }

}
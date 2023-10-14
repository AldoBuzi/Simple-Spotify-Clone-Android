package unipi.sam.emusic.commons

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import unipi.sam.emusic.models.TrackModel
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DownloadManager @Inject constructor(@ApplicationContext private var context: Context) {

    private var  downloadManager : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // Ottieni la directory dei file privati dell'applicazione
    private var privateDir : File? = context.getExternalFilesDir(null)

    suspend fun downloadTrack(track: TrackModel, callback: DownloadManagerCallback)  {
        val download = DownloadManager.Request(Uri.parse(track.url))
        download.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val file = File(privateDir,"${track.title}.mp3")
        /**
         * The file has been already downloaded
         */
        if(file.exists()){
            callback.fileAlreadyExist()
            return
        }
        download.setDestinationUri(Uri.fromFile(file))
        val downloadId = downloadManager.enqueue(download)
        val query: DownloadManager.Query = DownloadManager.Query()
        var c: Cursor?
        query.setFilterByStatus(DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PENDING or DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_SUCCESSFUL)

        val scheduleTaskExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        /*This schedules a runnable task every second*/
        scheduleTaskExecutor.scheduleAtFixedRate(
            {
                c = downloadManager.query(query)
                if (c!!.moveToFirst()) {
                    val columnStatus = c!!.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = c!!.getInt( if(columnStatus <0) 0 else columnStatus )
                    when (status) {
                        DownloadManager.STATUS_PAUSED -> {}
                        DownloadManager.STATUS_PENDING -> {}
                        DownloadManager.STATUS_RUNNING -> {}
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            callback.onSuccess(filePath = file.absolutePath)
                            scheduleTaskExecutor.shutdown()
                        }
                        DownloadManager.STATUS_FAILED -> {
                            callback.onError()
                            scheduleTaskExecutor.shutdown()
                        }
                    }
                }
            },
            0,
            1,
            TimeUnit.SECONDS
        )
    }

    fun deleteTrack(track: TrackModel, callback: DownloadManagerCallback){
        val file = File(privateDir,"${track.title}.mp3")
        try {
            if (file.exists()) {
                file.delete()
                callback.onSuccess(EMPTY_PATH)
            }
        } catch (e: SecurityException) { e.printStackTrace(); callback.onError() }
          catch (e: IOException) { e.printStackTrace(); callback.onError() }
    }

    fun deleteByUrl(title: String, callback: DownloadManagerCallback){
        val file = File(privateDir,"${title}.mp3")
        try {
            if (file.exists()) {
                file.delete()
                callback.onSuccess(EMPTY_PATH)
            }
        } catch (e: SecurityException) { e.printStackTrace(); callback.onError() }
        catch (e: IOException) { e.printStackTrace(); callback.onError() }
    }
    interface DownloadManagerCallback{
        fun onSuccess(filePath:String)
        fun onError()

        fun fileAlreadyExist()
    }
    val EMPTY_PATH = "/NO_PATH"
}
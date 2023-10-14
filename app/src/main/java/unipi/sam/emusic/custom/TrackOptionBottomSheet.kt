package unipi.sam.emusic.custom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.Component
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unipi.sam.emusic.R
import unipi.sam.emusic.api.API
import unipi.sam.emusic.commons.DownloadManager
import unipi.sam.emusic.commons.SnackBarProvider
import unipi.sam.emusic.components.playlist_detail.PlaylistDetailViewModel
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.room.PlaylistAndTrack
import javax.inject.Inject
import javax.inject.Singleton




@AndroidEntryPoint
class TrackOptionBottomSheet @Inject constructor(val activity: MainActivity, val item : TrackModel) : BottomSheetDialogFragment() {
    @Inject lateinit var database: AppDatabase
    @Inject lateinit var downloadManager : DownloadManager

    lateinit var root : View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(
            R.layout.track_options_dialog, container,
            false
        )
        root.findViewById<LinearLayout>(R.id.trackContainerSeeArtistBtn).setOnClickListener {
            activity.presentArtistDetailPage(ArtistModel(id = item.artist?.id, name = item.artist?.name, picture = API.buildPathFromMd5(item.image.toString())))
            this@TrackOptionBottomSheet.dismiss()
        }
        lifecycleScope.launch (Dispatchers.Main) {
            val data = database.playlistDao().getEverything()
            showLoadedData(data)
            setAddBtn(root.findViewById<LinearLayout>(R.id.trackContainerAddBtn),data)
        }
        return  root
    }

    fun showLoadedData(data: List<PlaylistAndTrack>){
        var alreadyDownloaded : Boolean? = null
        data.forEach { x ->
            for(key in x.tracks){
                if(key.id == item.id)
                    alreadyDownloaded = key.localUrl != null
            }
        }
        val downloadContainer = root.findViewById<LinearLayout>(R.id.trackContainerDownloadTrack)
        if(alreadyDownloaded == null) {
            downloadContainer.setOnClickListener {
                SnackBarProvider.getSnackBar(activity, "Devi prima aggiungere la canzone ad una playlist")
                    .show()
                this@TrackOptionBottomSheet.dismiss()
            }
            return
        }
        if(alreadyDownloaded == true){
            (downloadContainer.getChildAt(0) as ImageButton).setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.delete_download,null))
            (downloadContainer.getChildAt(1) as TextView).text = resources.getString(R.string.delete_track_from_storage)
            downloadContainer.setOnClickListener {
                lifecycleScope.launch (Dispatchers.Unconfined){
                    downloadManager.deleteTrack(item, object : DownloadManager.DownloadManagerCallback{
                        override fun onSuccess(filePath: String) {
                            lifecycleScope.launch (Dispatchers.Unconfined) {
                                database.trackDao().updateAll(null,item.id.toString())
                            }
                            PlaylistDetailViewModel.needRefresh.postValue(true)
                            lifecycleScope.launch (Dispatchers.Main) {
                                SnackBarProvider.getSnackBar(activity,"Canzone eliminata con successo")
                                    .show()
                            }
                        }
                        override fun onError() {
                            lifecycleScope.launch (Dispatchers.Main) {
                                SnackBarProvider.getSnackBar(activity,"Errore: il file potrebbe non esistere")
                                    .show()
                            }
                        }
                        override fun fileAlreadyExist() {/*not called*/ }
                    })
                }
                this@TrackOptionBottomSheet.dismiss()
            }
            return
        }
        downloadContainer.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Main) {
                downloadManager.downloadTrack(
                    item,
                    object : DownloadManager.DownloadManagerCallback {
                        override fun onError() {
                            SnackBarProvider.getSnackBar(
                                activity,
                                "Errore durante il download del file"
                            )
                                .show()
                        }
                        override fun onSuccess(filePath: String) {
                            lifecycleScope.launch (Dispatchers.Unconfined) {
                                database.trackDao().updateAll(filePath,item.id.toString())
                                item.apply { localUrl = filePath }
                            }
                            SnackBarProvider.getSnackBar(activity, "Canzone scaricata con successo")
                                .show()
                        }
                        override fun fileAlreadyExist() {
                            //Impossible Error
                            SnackBarProvider.getSnackBar(activity, "Errore inaspettato")
                                .show()
                        }
                    })
            }
            this@TrackOptionBottomSheet.dismiss()
        }
    }

    private fun setAddBtn(btn: LinearLayout, data: List<PlaylistAndTrack>){
        if (PlaylistDetailViewModel.playlistID != null) {
            (root.findViewById<LinearLayout>(R.id.trackContainerAddBtn).getChildAt(0) as ImageButton).setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.delete,null))
            (root.findViewById<LinearLayout>(R.id.trackContainerAddBtn).getChildAt(1) as TextView).text = resources.getString(R.string.option_remove_from_playlist)
        }
        btn.setOnClickListener {
            if(PlaylistDetailViewModel.playlistID == null) {
                val playlistSheet = PlaylistBottomSheet.newInstance(item)
                playlistSheet.show(requireActivity().supportFragmentManager,"Playlist Bottom Sheet")
            }
            else {
                lifecycleScope.launch  (Dispatchers.Unconfined){
                    val localUrl = item.localUrl
                    val result = database.trackDao().deleteByPrimaryKey(item.id.toString(),item.playlistID)
                    if(result >= 1 ) {
                        PlaylistDetailViewModel.needRefresh.postValue(true)
                        SnackBarProvider.getSnackBar(activity,"Canzone rimossa con successo")
                            .show()
                        if(localUrl != null)
                            lifecycleScope.launch (Dispatchers.Main) {
                                downloadManager.deleteByUrl(item.title!!,object : DownloadManager.DownloadManagerCallback{
                                    override fun onSuccess(filePath: String) {
                                        Log.d("DownloadManager","track removed")
                                    }
                                    override fun onError() {
                                        Log.e("DownloadManager","error removing track from storage")
                                    }
                                    override fun fileAlreadyExist() {}

                                })
                            }
                    }

                }
            }
            this@TrackOptionBottomSheet.dismiss()
        }
    }

    companion object {
        fun newInstance(activity: MainActivity,item: TrackModel): TrackOptionBottomSheet {
            return TrackOptionBottomSheet(activity,item)
        }
    }
}
package unipi.sam.emusic.components.playlist_detail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject


@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(private var model: PlaylistDetailModel): BaseViewModel() {

    var item: PlaylistModel ? = null
        set(value) {
            field = value
            playlistID = value!!.databaseId
        }
    @Inject lateinit var database: AppDatabase

    var hasDataLoaded = false

    suspend fun loadTracks(){
        if(item != null && item?.isFromDatabase == true) {
            val data = database.playlistDao().getTracksById(item!!.databaseId).tracks
            _widgets.postValue(mutableSetOf(Widget(data, LayoutType.TRACKS_LIST)))
            needRefresh.postValue(false)
            hasDataLoaded = true
            return
        }
        else if(item != null && item?.isFromDatabase == false){
            hasDataLoaded = true
            model.retrieveTracks(_widgets,item!!.tracklist!!)
        }
    }

    //UI Related
    var btnStartingPosition : Float? = null
        set(value) {
            if(field == null) field = value
        }
    val title: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isPlaylistSaved : MutableLiveData<Boolean>  by lazy { MutableLiveData<Boolean>() }
    private val isClicked : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    var showSnackBar: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    fun handlePlayButton() : MutableList<TrackModel>{
        if(widgets.value?.isEmpty() == true) return mutableListOf()
        return widgets.value?.elementAt(0)?.items as MutableList<TrackModel>
    }

    private suspend fun setupUI(){
        if(item?.title == null) return
        title.postValue(item!!.title)
        isPlaylistSaved.postValue(database.playlistDao().getPlaylistById(item!!.id.toString()).isNotEmpty())
    }

     suspend fun handleAddBtn(){
         isClicked.postValue(true)
         if (database.playlistDao().getPlaylistById(item!!.id.toString()).isEmpty()) {
             if( database.playlistDao().save(item!!) != 0L)
                 isPlaylistSaved.postValue(true)
         }
         else if (database.playlistDao().deleteById(item!!.id!!) == 1)
             isPlaylistSaved.postValue(false)
    }
    override suspend fun onCreate(){
        showSnackBar.addSource(isClicked) {_ ->  if(isClicked.value == true && isPlaylistSaved.value != null) showSnackBar.postValue( isPlaylistSaved.value) }
        showSnackBar.addSource(isPlaylistSaved) {_ -> if(isClicked.value == true && isPlaylistSaved.value != null) showSnackBar.postValue( isPlaylistSaved.value) }
        viewModelScope.launch (Dispatchers.Main) {
            setupUI()
        }
    }
    fun onDestroy(){
        playlistID = null
    }
    companion object{
        val needRefresh : MutableLiveData<Boolean> = MutableLiveData(false)
        //used only to check if is null, if null then it's remote playlist
        var playlistID : Int? = null
            private set
    }
}
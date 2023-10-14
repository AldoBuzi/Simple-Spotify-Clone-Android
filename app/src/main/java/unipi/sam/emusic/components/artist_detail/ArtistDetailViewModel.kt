package unipi.sam.emusic.components.artist_detail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class ArtistDetailViewModel @Inject constructor (private var artistDetailModel : ArtistDetailModel) : BaseViewModel() {

    @Inject lateinit var database: AppDatabase
    var item : ArtistModel? = null
        set(value) {
            field = value
            setupUI()
        }

    val picture : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val artistName: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val isArtistSaved : MutableLiveData<Boolean>  by lazy { MutableLiveData<Boolean>() }
    private val isClicked : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    var showSnackBar: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>()
    var isDataLoaded : Boolean = false
        private set


    override suspend fun onCreate() {
    }
    suspend fun loadTracks(){
        if(item == null) return
        artistDetailModel.retrieveTracks(_widgets, item!!.id.toString())
        isDataLoaded = true
    }

    fun loadAlbums(){
        if(item == null) return
        artistDetailModel.retrieveAlbums(_widgets, item!!.id.toString())
        isDataLoaded = true
    }

    fun loadPlaylists(){
        if(item == null) return
        artistDetailModel.retrievePlaylists(_widgets, item!!.id.toString())
        isDataLoaded = true
    }
    fun loadRelated(){
        if(item == null) return
        artistDetailModel.retrieveRelated(_widgets, item!!.id.toString())
        isDataLoaded = true
    }

    private fun setupUI(){
        showSnackBar.addSource(isClicked) {_ ->  if(isClicked.value == true && isArtistSaved.value != null) showSnackBar.postValue( isArtistSaved.value) }
        showSnackBar.addSource(isArtistSaved) {_ -> if(isClicked.value == true && isArtistSaved.value != null) showSnackBar.postValue( isArtistSaved.value) }
        if(item?.picture != null) picture.value = item!!.picture
        if(item?.name != null) artistName.value = item!!.name
        viewModelScope.launch (Dispatchers.Unconfined) {
            isArtistSaved.postValue(
                database.playlistDao().getPlaylistById(item!!.id.toString()).isNotEmpty()
            )
        }
    }

    suspend fun handleAddBtn(){
        isClicked.postValue(true)
        if (database.playlistDao().getPlaylistById(item!!.id.toString()).isEmpty()) {
            if( database.playlistDao().save(ArtistModel.getRoom(item!!)) != 0L)
                isArtistSaved.postValue(true)
        }
        else if (database.playlistDao().deleteById(item!!.id!!) == 1)
            isArtistSaved.postValue(false)
    }
    fun handlePlayButton(): MutableList<TrackModel>{
        if(widgets.value?.isEmpty() == true) return mutableListOf()
        val result = widgets.value?.firstOrNull { x -> x.viewType == LayoutType.TRACKS_LIST }
        if(result == null) return mutableListOf()
        return result.items as MutableList<TrackModel>
    }


}
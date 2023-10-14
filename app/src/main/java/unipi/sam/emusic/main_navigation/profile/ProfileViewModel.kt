package unipi.sam.emusic.main_navigation.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseViewModel
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.User
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(var model : ProfileModel): BaseViewModel() {

    @Inject lateinit var database: AppDatabase


    override suspend fun onCreate() {
    }

    suspend fun getPlaylists(){
        val data = database.playlistDao().getAll()
        _widgets.postValue(mutableSetOf(Widget(data, LayoutType.CUSTOM_PLAYLIST)))
        needRefresh.postValue(false)
    }

    suspend fun createPlaylist(playlistName : String){
        database.playlistDao().save(
            PlaylistModel(
                title = playlistName,
                id = -2,
                tracklist = null,
                type = "Playlist",
                user = User(),
                isFromDatabase = true
            )
        )
        getPlaylists()
    }

    companion object{
        val needRefresh : MutableLiveData<Boolean>  = MutableLiveData(false)
    }
}
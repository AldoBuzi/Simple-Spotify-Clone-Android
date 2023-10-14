package unipi.sam.emusic.main_module

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import unipi.sam.emusic.R
import unipi.sam.emusic.api.PAGES
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

class MainModel @Inject constructor() : BaseRemote(){
    var lastSearchCall: MutableList<Call<JsonObject>> = mutableListOf()


    fun retrieveSearch(widgets : MutableLiveData<MutableCollection<Widget>>, searchKeyWord: String){
        widgets.value = mutableListOf()
        lastSearchCall.forEach {x-> x.cancel() }
        lastSearchCall = mutableListOf()
        getSearchArtist(widgets, searchKeyWord)
        getSearchPlaylist(widgets, searchKeyWord)
        getSearchAlbum(widgets, searchKeyWord)
        getSearchTrack(widgets, searchKeyWord)
    }

    private fun getSearchArtist(widgets : MutableLiveData<MutableCollection<Widget>>, searchKeyWord: String){
        val call = apiService.getSearch(PAGES.artist, searchKeyWord)
        lastSearchCall.add(call)
        call.enqueue(baseCallback(LayoutType.ARTIST,widgets,resources.getString(R.string.artist_search)))

    }
    private fun getSearchPlaylist(widgets : MutableLiveData<MutableCollection<Widget>>, searchKeyWord: String){
        val call = apiService.getSearch(PAGES.playlist, searchKeyWord)
        lastSearchCall.add(call)
        call.enqueue(baseCallback(LayoutType.PLAYLIST,widgets,resources.getString(R.string.playlist_search)))
    }
    private fun getSearchTrack(widgets : MutableLiveData<MutableCollection<Widget>>, searchKeyWord: String){
        val call = apiService.getSearch(PAGES.track, searchKeyWord)
        lastSearchCall.add(call)
        call.enqueue(baseCallback(LayoutType.TRACKS_LIST,widgets,resources.getString(R.string.track_search)))
    }
    private fun getSearchAlbum(widgets : MutableLiveData<MutableCollection<Widget>>, searchKeyWord: String){
        val call = apiService.getSearch(PAGES.album, searchKeyWord)
        lastSearchCall.add(call)
        call.enqueue(baseCallback(LayoutType.ALBUMS,widgets,resources.getString(R.string.album_search)))
    }

}
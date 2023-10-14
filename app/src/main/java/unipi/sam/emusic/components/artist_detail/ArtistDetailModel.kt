package unipi.sam.emusic.components.artist_detail

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

class ArtistDetailModel @Inject constructor() : BaseRemote(){


    fun retrieveTracks(widgets: MutableLiveData<MutableCollection<Widget>>,artistId : String){
        val call = apiService.getArtistTopTracks(artistId)
        call.enqueue(baseCallback(LayoutType.TRACKS_LIST, widgets, resources.getString(R.string.track_artist_related)))
    }

    fun retrieveAlbums(widgets: MutableLiveData<MutableCollection<Widget>>,artistId : String){
        val call = apiService.getArtistTopAlbums(artistId)
        call.enqueue(baseCallback(LayoutType.ALBUMS,widgets,resources.getString(R.string.artist_best_album)))
    }

    fun retrievePlaylists(widgets: MutableLiveData<MutableCollection<Widget>>,artistId : String){
        val call = apiService.getArtistTopPlaylist(artistId)
        call.enqueue(baseCallback(LayoutType.PLAYLIST,widgets, resources.getString(R.string.artist_best_playlist)))
    }

    fun retrieveRelated(widgets: MutableLiveData<MutableCollection<Widget>>,artistId : String){
        val call = apiService.getArtistRelated(artistId)
        call.enqueue(baseCallback(LayoutType.ARTIST, widgets, resources.getString(R.string.artist_related)))
    }
}
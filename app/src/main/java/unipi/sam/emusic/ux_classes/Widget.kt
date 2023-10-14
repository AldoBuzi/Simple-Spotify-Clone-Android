package unipi.sam.emusic.ux_classes

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.GenreModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.PodcastModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.models.User
//import unipi.sam.emusic.room.Playlist
import unipi.sam.emusic.room.PlaylistAndTrack
import unipi.sam.emusic.room.RoomComponent
//import unipi.sam.emusic.room.Track

class Widget() {


    constructor(jsonObject: JsonObject?, layoutType: LayoutType, _heading:String) : this(){
        viewType = layoutType
        heading = _heading
        when(layoutType){
            LayoutType.ARTIST -> fetchArtist(jsonObject!!)
            LayoutType.ALBUMS -> fetchAlbums(jsonObject!!)
            LayoutType.TRACKS, LayoutType.TRACKS_LIST -> fetchTracks(jsonObject!!)
            LayoutType.ARTIST_ALBUM -> null
            LayoutType.PLAYLIST -> fetchPlaylist(jsonObject!!)
            LayoutType.PODCAST -> fetchPodcast(jsonObject!!)
            LayoutType.GENRES -> fetchGenres(jsonObject!!)
            else -> null
        }
    }
    var viewType: LayoutType? = null

    var data: ModelViewType? = null

    var items: MutableCollection<ModelViewType> = mutableListOf()

    var heading = ""


    constructor(component: List<RoomComponent>, layoutType: LayoutType) : this(){
        viewType = layoutType
        for(key in component){
            when(key){
                is PlaylistModel -> items.add(key)
                is TrackModel -> items.add(key)
                else -> null
            }
        }
    }
    private fun fetchGenres(jsonObject: JsonObject){
        fetchType<GenreModel>(jsonObject)
    }
    private fun fetchPodcast(jsonObject: JsonObject){
        fetchType<PodcastModel>(jsonObject)
    }
    private fun fetchPlaylist(jsonObject: JsonObject){
        fetchType<PlaylistModel>(jsonObject)
    }
    private fun fetchArtist(jsonObject: JsonObject){
        fetchType<ArtistModel>(jsonObject)
    }
    private fun fetchAlbums(jsonObject: JsonObject){
        fetchType<AlbumModel>(jsonObject)
    }
    private fun fetchTracks(jsonObject: JsonObject){
        fetchType<TrackModel>(jsonObject)

    }
    inline fun <reified T : ModelViewType>fetchType(jsonObject: JsonObject){
        if(!jsonObject.has("data")) return
        if (!jsonObject.get("data").isJsonArray) return
        for(key in jsonObject.get("data").asJsonArray) {
            items.add(Gson().fromJson(key,T::class.java))
        }
    }
}
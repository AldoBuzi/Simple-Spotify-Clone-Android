package unipi.sam.emusic.main_navigation.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import dagger.hilt.android.scopes.FragmentScoped
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.Application
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.ux_classes.Widget
import java.lang.Exception
import javax.inject.Inject

class HomeModel @Inject constructor(): BaseRemote() {

    var widget : MutableCollection<Widget> = mutableListOf()
    fun retrieveHome(widgets: MutableLiveData<MutableCollection<Widget>>) {
        getTopChart(widgets)
    }


    private fun getTopChart(widgets: MutableLiveData<MutableCollection<Widget>>){
        val call: Call<JsonObject> = apiService.getChart()
        val callback = object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    val obj = response.body()
                    val trackObj = obj!!.asJsonObject.get("tracks").asJsonObject
                    val albumObj = obj.asJsonObject.get("albums").asJsonObject
                    val artistObj = obj.asJsonObject.get("artists").asJsonObject
                    val playlistObj = obj.asJsonObject.get("playlists").asJsonObject
                    val podcastObj = obj.asJsonObject.get("podcasts").asJsonObject
                    updateWidgets(Widget(trackObj, LayoutType.TRACKS, resources.getString(R.string.best_track)),
                        Widget(albumObj,LayoutType.ALBUMS, resources.getString(R.string.best_album)),
                        Widget(artistObj,LayoutType.ARTIST, resources.getString(R.string.best_artist)),
                        Widget(playlistObj,LayoutType.PLAYLIST, resources.getString(R.string.best_playlist)),
                        Widget(podcastObj,LayoutType.PODCAST, resources.getString(R.string.best_podcast)),
                        widgets = widgets)

                    return
                }
                updateWidgets(widgets = widgets)
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                updateWidgets(widgets = widgets)
            }
        }
        call.enqueue(callback)
    }

    private fun getAlbum(widgets: MutableLiveData<MutableCollection<Widget>>){
        val call: Call<JsonObject> = apiService.getAlbum()
        val callback = object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    val obj = response.body()
                    updateWidgets(Widget(obj!!.asJsonObject, LayoutType.ARTIST_ALBUM , ""), widgets = widgets)
                    //widgets.postValue(widgets2.apply { addAll(mutableListOf(Widget(obj!!.asJsonObject, LayoutType.ARTIST_ALBUM)))})
                    return
                }
                widgets.postValue(mutableListOf())
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                widgets.postValue(mutableListOf())
            }
        }
        call.enqueue(callback)
    }
}
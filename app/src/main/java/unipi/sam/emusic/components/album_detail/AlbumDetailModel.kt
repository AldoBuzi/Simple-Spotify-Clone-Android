package unipi.sam.emusic.components.album_detail

import androidx.lifecycle.MutableLiveData
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

class AlbumDetailModel @Inject constructor() : BaseRemote(){

    fun retrieveTrackList(widgets : MutableLiveData<MutableCollection<Widget>>, trackUrl : String? = null){
        val call = apiService.getTrackList(trackUrl!!)
        call.enqueue(baseCallback(LayoutType.TRACKS_LIST, widgets, resources.getString(R.string.album_tracks)))
    }
}
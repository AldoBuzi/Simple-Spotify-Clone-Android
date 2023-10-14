package unipi.sam.emusic.components.playlist_detail

import androidx.lifecycle.MutableLiveData
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

class PlaylistDetailModel @Inject  constructor(): BaseRemote() {


    fun retrieveTracks(widgets: MutableLiveData<MutableCollection<Widget>>, tracklist: String){
        val call = apiService.getTrackList(tracklist)
        call.enqueue(baseCallback(LayoutType.TRACKS_LIST,widgets))

    }
}
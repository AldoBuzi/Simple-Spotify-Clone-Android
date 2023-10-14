package unipi.sam.emusic.main_navigation.discovery

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import unipi.sam.emusic.R
import unipi.sam.emusic.api.PAGES
import unipi.sam.emusic.base_components.BaseRemote
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

class DiscoverModel @Inject constructor() : BaseRemote() {

    fun retrieveGenres(widgets : MutableLiveData<MutableCollection<Widget>>){
        val call = apiService.getGenres()
        call.enqueue(baseCallback(LayoutType.GENRES,widgets))
    }


}